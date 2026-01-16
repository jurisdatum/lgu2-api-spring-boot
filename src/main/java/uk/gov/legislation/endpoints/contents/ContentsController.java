package uk.gov.legislation.endpoints.contents;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.transform.Transforms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;
import static uk.gov.legislation.endpoints.document.DocumentController.*;

@RestController
public class ContentsController implements ContentsApi {

    private final Legislation marklogic;
    private final Transforms transforms;

    public ContentsController(Legislation marklogic, Transforms transforms) {
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /* CLML */

    private final BiConsumer<InputStream, OutputStream> transferToWrapper = (input, output) -> {
        try {
            input.transferTo(output);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    };

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentContentsClml(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transferToWrapper, APPLICATION_XML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentContentsClml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transferToWrapper, APPLICATION_XML_UTF8);
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentContentsAkn(String type, int year, int number, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentContentsAkn(String type, String monarch, String years, int number, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }

    /* JSON */

    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2toc);
    }

    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2toc);
    }

    /* Word (.docx) */

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
    private interface Transform<T> {
        T apply(String clml) throws Exception;
    }

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version,
            Locale locale, Transform<T> transform) throws Exception {
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getTableOfContents(type, year, number, version, Optional.of(language));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private ResponseEntity<StreamingResponseBody> fetchAndTransformToStream(String type, String year, int number,
            Optional<String> version, Locale locale, BiConsumer<InputStream, OutputStream> transform, MediaType mt) {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getTableOfContentsStream(type, year, number, version, Optional.of(language));
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
