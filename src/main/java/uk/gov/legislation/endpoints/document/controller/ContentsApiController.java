package uk.gov.legislation.endpoints.document.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.TableOfContents;
import uk.gov.legislation.endpoints.document.api.ContentsApi;
import uk.gov.legislation.endpoints.document.helper.ContentsHelper;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.endpoints.document.controller.DocumentApiController.DOCUMENT_NOT_FOUND_MESSAGE;

@RestController
public class ContentsApiController implements ContentsApi {

    private final ContentsHelper contentsHelper;


    public ContentsApiController(ContentsHelper contentsHelper) {
        this.contentsHelper = contentsHelper;
    }

    @Override
    public ResponseEntity<String> getDocumentContentsClml(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsHelper.fetchContentsXml(type, year, number, version))
                .map(xmlContent -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xmlContent))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }

    @Override
    public ResponseEntity<String> getDocumentContentsAkn(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsHelper.fetchContentsXml(type, year, number, version))
                .map(contentsHelper::transformToAkn)
                .map(aknXml -> ResponseEntity
                        .ok().contentType(MediaType.valueOf("application/akn+xml"))
                        .body(aknXml))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }

    @Override
    public ResponseEntity<TableOfContents> getDocumentContentsJson(String type, int year, int number, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
        return Optional.ofNullable(contentsHelper.fetchContentsXml(type, year, number, version))
                .map(contentsHelper::simplifyToTableOfContents)
                .map(jsonContent -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jsonContent))
                .orElseThrow(() ->
                        new NoDocumentException(String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number)));
    }
}
