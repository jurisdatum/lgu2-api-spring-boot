package uk.gov.legislation.endpoints.fragment;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.Fragment;
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
import static uk.gov.legislation.endpoints.document.DocumentController.APPLICATION_AKN_XML;
import static uk.gov.legislation.endpoints.document.DocumentController.MS_WORD;

@RestController
public class FragmentController implements FragmentApi {

    private final Legislation marklogic;
    private final Transforms transforms;

    public FragmentController(Legislation marklogic, Transforms transforms) {
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
    public ResponseEntity<StreamingResponseBody> getFragmentClml(String type, Integer year, Integer number, String section,
            Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, section, version, locale, transferToWrapper, MediaType.APPLICATION_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentClml(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, section, version, locale, transferToWrapper, MediaType.APPLICATION_XML);
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentAkn(String type, int year, int number, String section,
            Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, section, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }
    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentAkn(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, section, version, locale, transforms::clml2akn, APPLICATION_AKN_XML);
    }

    /* HTML */

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentHtml(String type, int year, int number, String section,
            Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, section, version, locale, transforms::clml2htmlStandalone, MediaType.TEXT_HTML);
    }
    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentHtml(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, section, version, locale, transforms::clml2htmlStandalone, MediaType.TEXT_HTML);
    }

    /* JSON */

    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, int year, int number, String section,
            Optional<String> version, Locale locale) throws Exception {
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, transforms::clml2fragment);
    }
    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, transforms::clml2fragment);
    }

    /* Word (.docx) */

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, int year, int number, String section, Optional<String> version, Locale locale) {
        return fetchAndTransformToStream(type, Integer.toString(year), number, section, version, locale, transforms::clml2docx, MS_WORD);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> docx(String type, String monarch, String years, int number, String section, Optional<String> version, Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(type, regnalYear, number, section, version, locale, transforms::clml2docx, MS_WORD);
    }

    /* helper */

    @FunctionalInterface
    private interface Transform<T> {
        T apply(String clml) throws Exception;
    }

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, String section,
            Optional<String> version, Locale locale, Transform<T> transform) throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getDocumentSection(type, year, number, section, version, Optional.of(String.valueOf(language)));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private ResponseEntity<StreamingResponseBody> fetchAndTransformToStream(String type, String year, int number, String section,
            Optional<String> version, Locale locale, BiConsumer<InputStream, OutputStream> transform, MediaType mt) {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
            marklogic.getDocumentSectionStream(type, year, number, section, version, Optional.of(String.valueOf(language)));
        StreamingResponseBody body = output -> {
            try (InputStream in = doc.clml()) {
                transform.accept(in, output);
            }
        };
        HttpHeaders headers = CustomHeaders.make(language, doc.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).contentType(mt).body(body);
    }

}
