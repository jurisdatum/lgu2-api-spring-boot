package uk.gov.legislation.endpoints.document;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Transforms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class DocumentController implements DocumentApi {

    private final Legislation marklogic;

    private final Transforms transforms;

    public DocumentController(Legislation marklogic, Transforms transforms) {
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /* CLML */

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentClml(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        return streamDocument(type, Integer.toString(year), number, version, locale, InputStream::transferTo, MediaType.APPLICATION_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentClml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return streamDocument(type, regnalYear, number, version, locale, InputStream::transferTo, MediaType.APPLICATION_XML);
    }

    /* Akoma Ntoso */

    private StreamingTransform aknTransform() {
        return (clml, akn) -> {
            try {
                transforms.clml2akn(clml, akn);
            } catch (SaxonApiException e) {
                throw new TransformationException("AkN transform failed", e);
            }
        };
    }

    private static final MediaType APPLICATION_AKN_XML = MediaType.parseMediaType("application/akn+xml");

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentAkn(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        return streamDocument(type, Integer.toString(year), number, version, locale, aknTransform(), APPLICATION_AKN_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentAkn(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return streamDocument(type, regnalYear, number, version, locale, aknTransform(), APPLICATION_AKN_XML);
    }

    /* HTML */

    private StreamingTransform htmlTransform() {
        return (clml, html) -> {
            try {
                transforms.clml2html(clml, true, html);
            } catch (SaxonApiException e) {
                throw new TransformationException("HTML transform failed", e);
            }
        };
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentHtml(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        return streamDocument(type, Integer.toString(year), number, version, locale, htmlTransform(), MediaType.TEXT_HTML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getDocumentHtml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return streamDocument(type, regnalYear, number, version, locale, htmlTransform(), MediaType.TEXT_HTML);
    }

    /* JSON */

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentStream(type, Integer.toString(year), number, version, Optional.of(language));
        Document json;
        try (InputStream clml = doc.clml()) {
            json = transforms.clml2document(clml);
        }
        HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentStream(type, regnalYear, number, version, Optional.of(language));
        Document json;
        try (InputStream clml = doc.clml()) {
            json = transforms.clml2document(clml);
        }
        HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    /* Word (.docx) */

    private StreamingTransform wordTransform() {
        return (clml, docx) -> {
            try {
                transforms.clml2docx(clml, docx);
            } catch (SaxonApiException e) {
                throw new TransformationException("Word transform failed", e);
            }
        };
    }

    private static final MediaType MS_WORD = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        return streamDocument(type, Integer.toString(year), number, version, locale, wordTransform(), MS_WORD);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return streamDocument(type, regnalYear, number, version, locale, wordTransform(), MS_WORD);
    }

    /* helper */

    @FunctionalInterface
    public interface Transform<T> { T apply(String t) throws Exception; }

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version, Locale locale, Transform<T> transform) throws Exception {
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getDocument(type, year, number, version, Optional.of(language));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

    @FunctionalInterface
    public interface StreamingTransform {
        void accept(InputStream t, OutputStream u) throws IOException;
    }

    private ResponseEntity<StreamingResponseBody> streamDocument(String type, String year, int number, Optional<String> version, Locale locale, StreamingTransform transform, MediaType mt) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentStream(type, year, number, version, Optional.of(language));
        StreamingResponseBody body = output -> {
            try (InputStream in = doc.clml()) {
                transform.accept(in, output);
            }
        };
        HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).contentType(mt).body(body);
    }

}
