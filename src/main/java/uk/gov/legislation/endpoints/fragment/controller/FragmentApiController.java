package uk.gov.legislation.endpoints.fragment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.endpoints.fragment.api.FragmentApi;
import uk.gov.legislation.endpoints.fragment.service.FragmentService;
import uk.gov.legislation.endpoints.fragment.service.TransformationService;
import java.util.Optional;
import java.util.function.Function;

/**
 * REST Controller for managing fragment retrieval and transformation APIs.
 * This controller provides endpoints to fetch document fragments in various formats such as CLML, AKN, HTML, and JSON.
 */
@RestController
public class FragmentApiController implements FragmentApi {

    private final FragmentService fragmentService;
    private final TransformationService transformationService;

    /**
     * Constructs a new FragmentApiController with the given fragment and transformation services.
     *
     * @param fragmentService        Service responsible for document retrieval
     * @param transformationService  Service responsible for transforming documents into various formats
     */
    public FragmentApiController(FragmentService fragmentService, TransformationService transformationService) {
        this.fragmentService = fragmentService;
        this.transformationService = transformationService;
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
    public ResponseEntity<String> getFragmentClMl(String type, int year, int number, String section, Optional<String> version) {
        return getFragmentClMls(type, Integer.toString(year), number, section, version);
    }
    /**
     * @param monarch   An abbreviation of the monarch, relative to which the year is given, e.g., 'Vict'
     * @param years     A year or range of years, relative to the monarch, e.g., '1' or '1-2'
     */
    @Override
    public ResponseEntity<String> getFragmentClMl(String type, String monarch, String years, int number, String section, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getFragmentClMls(type, regnalYear, number, section, version);
    }
    private ResponseEntity<String> getFragmentClMls(String type, String year, int number, String section, Optional<String> version) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, Function.identity());
    }

    /**
     * Retrieves a document fragment in AKN (Akoma Ntoso) XML format.
     * Transforms the retrieved CLML content to AKN format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, int year, int number, String section, Optional<String> version) {
        return getFragmentAkn(type, Integer.toString(year), number, section, version);
    }
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, String monarch, String years, int number, String section, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getFragmentAkn(type, regnalYear, number, section, version);
    }
    private ResponseEntity<String> getFragmentAkn(String type, String year, int number, String section, Optional<String> version) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, transformationService::transformToAkn);
    }

    /**
     * Retrieves a document fragment in HTML format.
     * Transforms the retrieved CLML content to HTML format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, int year, int number, String section, Optional<String> version) {
        return getFragmentHtml(type, Integer.toString(year), number, section, version);
    }
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, String monarch, String years, int number, String section, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getFragmentHtml(type, regnalYear, number, section, version);
    }
    private ResponseEntity<String> getFragmentHtml(String type, String year, int number, String section, Optional<String> version) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, clml -> transformationService.transformToHtml(clml, true));
    }

    /**
     * Retrieves a document fragment in JSON format.
     * Transforms the retrieved CLML content to JSON format using the transformation service.
     */
    @Override
    public ResponseEntity<DocumentApi.Response> getFragmentJson(String type, int year, int number, String section, Optional<String> version) {
        return getFragmentJson(type, Integer.toString(year), number, section, version);
    }
    @Override
    public ResponseEntity<DocumentApi.Response> getFragmentJson(String type, String monarch, String years, int number, String section, Optional<String> version) {
        String regnalYear = String.join("/", monarch, years);
        return getFragmentJson(type, regnalYear, number, section, version);
    }
    private ResponseEntity<DocumentApi.Response> getFragmentJson(String type, String year, int number, String section, Optional<String> version) {
        return fragmentService.fetchAndTransform(type, year, number, section, version, transformationService::createJsonResponse);
    }

}
