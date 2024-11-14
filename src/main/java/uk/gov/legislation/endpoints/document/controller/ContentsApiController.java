package uk.gov.legislation.endpoints.document.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.TableOfContents;
import uk.gov.legislation.endpoints.document.api.ContentsApi;
import uk.gov.legislation.endpoints.document.service.ContentsService;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.endpoints.document.controller.DocumentApiController.DOCUMENT_NOT_FOUND_MESSAGE;

/**
 * REST Controller for managing API endpoints related to document contents.
 * This controller provides methods to retrieve document contents in various formats: CLML, AKN, and JSON.
 */
@RestController
public class ContentsApiController implements ContentsApi {

    private final ContentsService contentsService;
    /**
     * Constructor that initializes the ContentsApiController with the required contents service.
     *
     * @param contentsService  Service that provides methods to fetch and transform document contents
     */
    public ContentsApiController(ContentsService contentsService) {
        this.contentsService = contentsService;
    }

    /**
     * Retrieves the document contents in CLML (Custom Markup Language) XML format.
     *
     * @param type     The document type identifier
     * @param year     The publication year of the document
     * @param number   The document number
     * @param version  Optional version of the document to retrieve
     * @return ResponseEntity containing CLML XML if found, or throws NoDocumentException if the document is not found
     * @throws IOException             if an I/O error occurs during document retrieval
     * @throws InterruptedException    if the operation is interrupted
     * @throws NoDocumentException     if the document is not found
     */

    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsService.fetchContentsXml(type, year, number, version))
                .map(xmlContent -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xmlContent))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }

    /**
     * Retrieves the document contents in AKN (Akoma Ntoso) XML format.
     * Converts the CLML format to AKN XML format using the contents service.
     */
    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsService.fetchContentsXml(type, year, number, version))
                .map(contentsService::transformToAkn)
                .map(aknXml -> ResponseEntity
                        .ok().contentType(MediaType.valueOf("application/akn+xml"))
                        .body(aknXml))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }

    /**
     * Retrieves the document contents in a simplified JSON format.
     * Converts the CLML format to a TableOfContents object using the contents service.
     */
    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsService.fetchContentsXml(type, year, number, version))
                .map(contentsService::simplifyToTableOfContents)
                .map(jsonContent -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonContent))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }
}
