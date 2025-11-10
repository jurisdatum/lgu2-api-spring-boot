package uk.gov.legislation.endpoints.contents;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.transform.Transforms;

import java.util.Locale;
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class ContentsController implements ContentsApi {

    private final Legislation marklogic;
    private final Transforms transforms;

    public ContentsController(Legislation marklogic, Transforms transforms) {
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /* CLML */

    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, clml -> clml);
    }

    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, clml -> clml);
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2akn);
    }

    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2akn);
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
    public ResponseEntity<byte[]> docx(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2docx);
    }
    @Override
    public ResponseEntity<byte[]> docx(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2docx);
    }

    /* helper */

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

}
