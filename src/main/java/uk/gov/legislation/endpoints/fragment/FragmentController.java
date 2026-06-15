package uk.gov.legislation.endpoints.fragment;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;
import static uk.gov.legislation.endpoints.document.DocumentController.APPLICATION_AKN_XML;
import static uk.gov.legislation.endpoints.document.DocumentController.APPLICATION_XML_UTF8;
import static uk.gov.legislation.endpoints.document.DocumentController.MS_WORD;
import static uk.gov.legislation.endpoints.document.DocumentController.TEXT_HTML_UTF8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.exceptions.NoDocumentException;
import uk.gov.legislation.transform.Transforms;

@RestController
public class FragmentController implements FragmentApi {

    private final Legislation marklogic;
    private final Transforms transforms;

    public FragmentController(Legislation marklogic, Transforms transforms) {
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /* CLML */

    private void transferToWrapper(InputStream input, OutputStream output) {
        try {
            input.transferTo(output);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentClml(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        return fetchAndTransformToStream(
                type,
                Integer.toString(year),
                number,
                section,
                version,
                locale,
                this::transferToWrapper,
                APPLICATION_XML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentClml(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(
                type,
                regnalYear,
                number,
                section,
                version,
                locale,
                this::transferToWrapper,
                APPLICATION_XML_UTF8);
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentAkn(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        return fetchAndTransformToStream(
                type,
                Integer.toString(year),
                number,
                section,
                version,
                locale,
                transforms::clml2akn,
                APPLICATION_AKN_XML);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentAkn(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(
                type,
                regnalYear,
                number,
                section,
                version,
                locale,
                transforms::clml2akn,
                APPLICATION_AKN_XML);
    }

    /* HTML */

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentHtml(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        return fetchAndTransformToStream(
                type,
                Integer.toString(year),
                number,
                section,
                version,
                locale,
                transforms::clml2htmlStandalone,
                TEXT_HTML_UTF8);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getFragmentHtml(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(
                type,
                regnalYear,
                number,
                section,
                version,
                locale,
                transforms::clml2htmlStandalone,
                TEXT_HTML_UTF8);
    }

    /* JSON */

    @Override
    public ResponseEntity<Fragment> getFragmentJson(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale)
            throws Exception {
        return fetchAndTransform(
                type,
                Integer.toString(year),
                number,
                section,
                version,
                locale,
                transforms::clml2fragment);
    }

    @Override
    public ResponseEntity<Fragment> getFragmentJson(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale)
            throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(
                type, regnalYear, number, section, version, locale, transforms::clml2fragment);
    }

    /* Word (.docx) */

    @Override
    public ResponseEntity<StreamingResponseBody> docx(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        return fetchAndTransformToStream(
                type,
                Integer.toString(year),
                number,
                section,
                version,
                locale,
                transforms::clml2docx,
                MS_WORD);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> docx(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransformToStream(
                type, regnalYear, number, section, version, locale, transforms::clml2docx, MS_WORD);
    }

    /* HEAD (existence check) */

    @Override
    public ResponseEntity<Void> headFragment(
            String type,
            int year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        validateType(type);
        return fragmentExists(type, Integer.toString(year), number, section, version, locale)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> headFragment(
            String type,
            String monarch,
            String years,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fragmentExists(type, regnalYear, number, section, version, locale)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Resolves existence through the same path GET uses ({@link Legislation#getDocumentSection}),
     * passing the same {@code version} and language, so HEAD and GET evaluate existence for the
     * same fragment at the same point in time and cannot disagree. The lightweight {@code
     * legislation-by-id.xq} resolver only confirms that the <em>document</em> resolves — it returns
     * a 303 for a well-formed but non-existent section — so it cannot answer fragment existence.
     */
    private boolean fragmentExists(
            String type,
            String year,
            int number,
            String section,
            Optional<String> version,
            Locale locale) {
        try {
            marklogic.getDocumentSection(
                    type, year, number, section, version, Optional.of(locale.getLanguage()));
            return true;
        } catch (NoDocumentException e) {
            return false;
        }
    }

    /* helper */

    @FunctionalInterface
    private interface Transform<T> {
        T apply(String clml) throws Exception;
    }

    private <T> ResponseEntity<T> fetchAndTransform(
            String type,
            String year,
            int number,
            String section,
            Optional<String> version,
            Locale locale,
            Transform<T> transform)
            throws Exception {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.Response leg =
                marklogic.getDocumentSection(
                        type, year, number, section, version, Optional.of(language));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private ResponseEntity<StreamingResponseBody> fetchAndTransformToStream(
            String type,
            String year,
            int number,
            String section,
            Optional<String> version,
            Locale locale,
            BiConsumer<InputStream, OutputStream> transform,
            MediaType mt) {
        validateType(type);
        String language = locale.getLanguage();
        Legislation.StreamResponse doc =
                marklogic.getDocumentSectionStream(
                        type, year, number, section, version, Optional.of(language));
        InputStream clml = doc.clml();
        try {
            StreamingResponseBody body =
                    output -> {
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
