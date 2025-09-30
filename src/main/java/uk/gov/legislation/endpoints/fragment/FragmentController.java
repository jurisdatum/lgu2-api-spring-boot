package uk.gov.legislation.endpoints.fragment;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.endpoints.document.DocumentController.Transform;
import uk.gov.legislation.transform.Transforms;

import java.util.Locale;
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class FragmentController implements FragmentApi {

    private final Legislation marklogic;
    private final Transforms transforms;

    public FragmentController(Legislation marklogic, Transforms transforms) {
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /* CLML */

    @Override
    public ResponseEntity<String> getFragmentClml(String type, Integer year, Integer number, String section,
            Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, clml -> clml);
    }

    @Override
    public ResponseEntity<String> getFragmentClml(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, clml -> clml);
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<String> getFragmentAkn(String type, int year, int number, String section,
            Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, transforms::clml2akn);
    }
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, transforms::clml2akn);
    }

    /* HTML */

    @Override
    public ResponseEntity<String> getFragmentHtml(String type, int year, int number, String section,
            Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        Transform<String> transform = clml -> transforms.clml2html(clml, true);
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, transform);
    }
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        Transform<String> transform = clml -> transforms.clml2html(clml, true);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, transform);
    }

    /* JSON */

    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, int year, int number, String section,
            Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, transforms::clml2fragment);
    }
    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, String monarch, String years, int number,
            String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, transforms::clml2fragment);
    }

    /* Word (.docx) */

    @Override
    public ResponseEntity<byte[]> docx(String type, int year, int number, String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, section, version, locale, transforms::clml2docx);
    }

    @Override
    public ResponseEntity<byte[]> docx(String type, String monarch, String years, int number, String section, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, section, version, locale, transforms::clml2docx);
    }

    /* helper */

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, String section,
            Optional<String> version, Locale locale, Transform<T> transform) throws Exception {
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getDocumentSection(type, year, number, section, version, Optional.of(String.valueOf(language)));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
