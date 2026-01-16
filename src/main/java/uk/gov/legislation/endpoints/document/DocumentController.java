package uk.gov.legislation.endpoints.document;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.Associated;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.converters.ImpactAssessmentConverter;
import uk.gov.legislation.data.marklogic.impacts.Impacts;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.transform.Transforms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class DocumentController implements DocumentApi {

    private final Legislation marklogic;

    private final Transforms transforms;

    private final Impacts impacts;

    public DocumentController(Legislation marklogic, Transforms transforms, Impacts impacts) {
        this.marklogic = marklogic;
        this.transforms = transforms;
        this.impacts = impacts;
    }

    /* CLML */

    private final BiConsumer<InputStream, OutputStream> transferToWrapper = (input, output) -> {
        try {
            input.transferTo(output);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    };

    public static final MediaType APPLICATION_XML_UTF8 = new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8);

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentClml(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transferToWrapper, APPLICATION_XML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentClml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transferToWrapper, APPLICATION_XML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getImpactAssessmentClml(int year, int number) throws Exception {
        return impacts.getStream(year, number)
            .map(input -> {
                try {
                    StreamingResponseBody body = output -> {
                        try (input) { input.transferTo(output); }
                    };
                    return ResponseEntity.ok().contentType(APPLICATION_XML_UTF8).body(body);
                } catch (RuntimeException e) {
                    try {
                        input.close();
                    } catch (IOException closeException) {
                        e.addSuppressed(closeException);
                    }
                    throw e;
                }
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /* Akoma Ntoso */

    public static final MediaType APPLICATION_AKN_XML = MediaType.parseMediaType("application/akn+xml;charset=UTF-8");

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentAkn(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentAkn(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }

    /* HTML */

    public static final MediaType TEXT_HTML_UTF8 = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentHtml(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transforms::clml2htmlStandalone, TEXT_HTML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentHtml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transforms::clml2htmlStandalone, TEXT_HTML_UTF8);
    }

    /* JSON */

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2document);
    }

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2document);
    }

    @Override
    public ResponseEntity<Associated> getImpactAssessmentJson(int year, int number) throws Exception {
        return impacts.get(year, number)
            .map(ImpactAssessmentConverter::convert)
            .map(associated -> ResponseEntity.ok().body(associated))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /* Word (.docx) */

    public static final MediaType MS_WORD = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transforms::clml2docx, MS_WORD);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transforms::clml2docx, MS_WORD);
    }

    /* helpers */

    @FunctionalInterface
    public interface Transform<T> { T apply(InputStream t) throws Exception; }

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version, Locale locale, Transform<T> transform) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentStream(type, year, number, version, Optional.of(language));
        T body;
        try (InputStream clml = doc.clml()) {
            body = transform.apply(clml);
        }
        HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private ResponseEntity<StreamingResponseBody> fetchAndTransformToStream(String type, String year, int number, Optional<String> version, Locale locale, BiConsumer<InputStream, OutputStream> transform, MediaType mt) {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentStream(type, year, number, version, Optional.of(language));
        InputStream clml = doc.clml();
        try {
            StreamingResponseBody body = output -> {
                try (InputStream in = clml) {
                    transform.accept(in, output);
                }
            };
            HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
            return ResponseEntity.ok().headers(headers).contentType(mt).body(body);
        } catch (RuntimeException e) {
            try {
                clml.close();
            } catch (IOException closeException) {
                e.addSuppressed(closeException);
            }
            throw e;
        }
    }

}
