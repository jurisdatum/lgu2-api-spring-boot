package uk.gov.legislation.endpoints.fragment.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.endpoints.fragment.api.FragmentApi;
import uk.gov.legislation.endpoints.fragment.service.FragmentService;
import uk.gov.legislation.endpoints.fragment.service.TransformationService;
import uk.gov.legislation.transform.Transforms;

import java.util.Optional;
import java.util.function.Function;

import static uk.gov.legislation.endpoints.ParameterValidator.validateLanguage;


/**
 * REST Controller for managing fragment retrieval and transformation APIs.
 * This controller provides endpoints to fetch document fragments in various formats such as CLML, AKN, HTML, and JSON.
 */
@RestController
public class FragmentApiController implements FragmentApi {

    private final FragmentService fragmentService;
    private final TransformationService transformationService;
    private final Legislation marklogic;
    private final Transforms transforms;

    public FragmentApiController(FragmentService fragmentService, TransformationService transformationService, Legislation marklogic, Transforms transforms) {
        this.fragmentService = fragmentService;
        this.transformationService = transformationService;
        this.marklogic = marklogic;
        this.transforms = transforms;
    }

    /**
     * Retrieves a document fragment in CLML (Custom Markup Language) format.
     *
     * @param type     The document type
     * @param year     The document year
     * @param number   The document number
     * @param section  The section of the document to retrieve
     * @param version  The version of the document (if available)
     * @return ResponseEntity with the CLML content if found, or 404 Not Found if the section is missing
     */
    @Override
    public ResponseEntity<String> getFragmentClml(String type, int year, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        return getFragmentClml(type, Integer.toString(year), number, section, version, language);
    }
    /**
     * @param monarch   An abbreviation of the monarch, relative to which the year is given, e.g., 'Vict'
     * @param years     A year or range of years, relative to the monarch, e.g., '1' or '1-2'
     */
    @Override
    public ResponseEntity<String> getFragmentClml(String type, String monarch, String years, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getFragmentClml(type, regnalYear, number, section, version, language);
    }
    private ResponseEntity<String> getFragmentClml(String type, String year, int number, String section, Optional<String> version, String validatedLanguage) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, Function.identity(), validatedLanguage);
    }

    /**
     * Retrieves a document fragment in AKN (Akoma Ntoso) XML format.
     * Transforms the retrieved CLML content to AKN format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, int year, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        return getFragmentAkn(type, Integer.toString(year), number, section, version, language);
    }
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, String monarch, String years, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getFragmentAkn(type, regnalYear, number, section, version, language);
    }
    private ResponseEntity<String> getFragmentAkn(String type, String year, int number, String section, Optional<String> version, String language) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, transformationService::transformToAkn, language);
    }

    /**
     * Retrieves a document fragment in HTML format.
     * Transforms the retrieved CLML content to HTML format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, int year, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        return getFragmentHtml(type, Integer.toString(year), number, section, version, language);
    }
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, String monarch, String years, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getFragmentHtml(type, regnalYear, number, section, version, language);
    }
    private ResponseEntity<String> getFragmentHtml(String type, String year, int number, String section, Optional<String> version, String validatedLanguage) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, clml -> transformationService.transformToHtml(clml, true), validatedLanguage);
    }

    /**
     * Retrieves a document fragment in JSON format.
     * Transforms the retrieved CLML content to JSON format using the transformation service.
     */
    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, int year, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        return getFragmentJson(type, Integer.toString(year), number, section, version, language);
    }
    @Override
    public ResponseEntity<Fragment> getFragmentJson(String type, String monarch, String years, int number, String section, Optional<String> version, String language) {
        validateLanguage(language);
        String regnalYear = String.join("/", monarch, years);
        return getFragmentJson(type, regnalYear, number, section, version, language);
    }
    private ResponseEntity<Fragment> getFragmentJson(String type, String year, int number, String section, Optional<String> version, String validatedLanguage) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, transformationService::transformToJsonResponse, validatedLanguage);
    }

    /* Word (.docx) */

    @Override
    public ResponseEntity<byte[]> docx(String type, int year, int number, String section, Optional<String> version, String language) throws Exception {
        return docx(type, Integer.toString(year), number, section, version, language);
    }

    @Override
    public ResponseEntity<byte[]> docx(String type, String monarch, String years, int number, String section, Optional<String> version, String language) throws Exception {
        String regnalYear = String.join("/", monarch, years);
        return docx(type, regnalYear, number, section, version, language);
    }

    private ResponseEntity<byte[]> docx(String type, String year, int number, String section, Optional<String> version, String language) throws Exception {
        validateLanguage(language);
        Legislation.Response leg = marklogic.getDocumentSection(type, year, number, section, version, Optional.of(language));
        byte[] docx = transforms.clml2docx(leg.clml());
        return CustomHeaders.ok(docx, leg.redirect());
    }

}
