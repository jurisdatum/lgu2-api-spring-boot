package uk.gov.legislation.endpoints.document;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.responses.Associated;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.converters.ImpactAssessmentConverter;
import uk.gov.legislation.data.marklogic.impacts.Impacts;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.transform.Transforms;

import java.util.Locale;
import java.util.Optional;

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

    @Override
    public ResponseEntity<String> getDocumentClml(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, clml -> clml);
    }

    @Override
    public ResponseEntity<String> getDocumentClml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, clml -> clml);
    }

    @Override
    public String getImpactAssessmentClml(int year, int number) throws Exception {
        return impacts.getXml(year, number)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /* Akoma Ntoso */

    @Override
    public ResponseEntity<String> getDocumentAkn(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2akn);
    }

    @Override
    public ResponseEntity<String> getDocumentAkn(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2akn);
    }

    /* HTML */

    @Override
    public ResponseEntity<String> getDocumentHtml(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        Transform<String> transform = clml -> transforms.clml2html(clml, true);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transform);
    }

    @Override
    public ResponseEntity<String> getDocumentHtml(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        Transform<String> transform = clml -> transforms.clml2html(clml, true);
        return fetchAndTransform(type, regnalYear, number, version, locale, transform);
    }

    /* JSON */

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, int year, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        return fetchAndTransform(type, Integer.toString(year), number, version, locale, transforms::clml2document);
    }

    @Override
    public ResponseEntity<Document> getDocumentJson(String type, String monarch, String years, int number, Optional<String> version, Locale locale) throws Exception {
        validateType(type);
        String regnalYear = String.join("/", monarch, years);
        return fetchAndTransform(type, regnalYear, number, version, locale, transforms::clml2document);
    }

    @Override
    public Associated getImpactAssessmentJson(int year, int number) throws Exception {
        return impacts.get(year, number)
            .map(ImpactAssessmentConverter::convert)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
    public interface Transform<T> { T apply(String t) throws Exception; }

    private <T> ResponseEntity<T> fetchAndTransform(String type, String year, int number, Optional<String> version, Locale locale, Transform<T> transform) throws Exception {
        String language = locale.getLanguage();
        Legislation.Response leg = marklogic.getDocument(type, year, number, version, Optional.of(language));
        T body = transform.apply(leg.clml());
        HttpHeaders headers = CustomHeaders.make(language, leg.redirect().orElse(null));
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
