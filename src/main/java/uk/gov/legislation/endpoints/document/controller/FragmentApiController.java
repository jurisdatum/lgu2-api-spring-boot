package uk.gov.legislation.endpoints.document.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.endpoints.document.api.FragmentApi;
import uk.gov.legislation.endpoints.document.service.FragmentService;
import uk.gov.legislation.endpoints.document.service.TransformationService;
import java.util.Optional;

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
    public ResponseEntity<String> getFragmentClml(String type, int year, int number, String section, Optional<String> version) {
        return fragmentService.getDocumentSection(type, year, number, section, version)
                .map(clml -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(clml))
                .orElseThrow(() -> new NoDocumentException(
                        fragmentService.getNotFoundMessage(type, year, number)));
    }

    /**
     * Retrieves a document fragment in AKN (Akoma Ntoso) XML format.
     * Transforms the retrieved CLML content to AKN format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentAkn(String type, int year, int number, String section, Optional<String> version) {
        return fragmentService.getDocumentSection(type, year, number, section, version)
                .map(transformationService::transformToAkn)
                .map(akn -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/akn+xml"))
                        .body(akn))
                .orElseThrow(() -> new NoDocumentException(
                        fragmentService.getNotFoundMessage(type, year, number)));
    }

    /**
     * Retrieves a document fragment in HTML format.
     * Transforms the retrieved CLML content to HTML format using the transformation service.
     */
    @Override
    public ResponseEntity<String> getFragmentHtml(String type, int year, int number, String section, Optional<String> version) {
        return fragmentService.getDocumentSection(type, year, number, section, version)
                .map(clml -> transformationService.transformToHtml(clml, true))
                .map(html -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html))
                .orElseThrow(() -> new NoDocumentException(
                        fragmentService.getNotFoundMessage(type, year, number)));
    }

    /**
     * Retrieves a document fragment in JSON format.
     * Transforms the retrieved CLML content to JSON format using the transformation service.
     */
    @Override
    public ResponseEntity<DocumentApi.Response> getFragmentJson(String type, int year, int number, String section, Optional<String> version) {
        return fragmentService.getDocumentSection(type, year, number, section, version)
                .map(transformationService::createJsonResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoDocumentException(
                        fragmentService.getNotFoundMessage(type, year, number)));
    }
}