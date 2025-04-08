package uk.gov.legislation.endpoints.opensearch.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.legislation.data.virtuoso.model.OpenSearchRequest;
import uk.gov.legislation.endpoints.opensearch.api.OpenSearchApi;
import uk.gov.legislation.endpoints.opensearch.service.OpenSearchService;

import java.io.IOException;
@RestController
public class OpenSearchApiController implements OpenSearchApi {
private final OpenSearchService service;

    public OpenSearchApiController(OpenSearchService service) {
        this.service = service;
    }
    /**
     * API to search documents based on any  of type, titles, year, and number.
     */

    @Override
    public ResponseEntity <String> getOpenSearch(String title, String type, Integer year, Integer number, String language, int page, int size) throws IOException {
        String jsonResult = service.get(title, type, year, number, language, page, size);
        return ResponseEntity.ok(jsonResult);
    }

    @Override
    public ResponseEntity<String> getDocument(String id) throws IOException {
        String result = service.getDocumentById(id);
        return ResponseEntity.ok(result);
    }


    @Override
    public ResponseEntity <String> getAllDocuments() throws IOException {
        String result = service.getAllDocuments();
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity <String> storeDocument(OpenSearchRequest document) throws IOException {
        String documentId = service.saveDocument(document);
        return ResponseEntity.ok("Document stored with ID: " + documentId);
    }

    @Override
    public ResponseEntity<String> deleteDocument(String id) throws IOException {
        String result = service.deleteDocument(id);
        return ResponseEntity.ok("Delete result: " + result);
    }
}




