package uk.gov.legislation.endpoints.document.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.document.api.DocumentsApi;
import uk.gov.legislation.endpoints.document.service.DocumentsService;
import uk.gov.legislation.endpoints.documents.DocumentList;

import java.io.IOException;

@RestController
public class DocumentsApiController implements DocumentsApi {

    private final DocumentsService documentsService;

    public DocumentsApiController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @Override
    public ResponseEntity<DocumentList> getDocs(String type, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getDocumentsByType(type, page));
    }

    @Override
    public ResponseEntity<String> getFeed(String type, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getFeedByType(type, page));
    }

    @Override
    public ResponseEntity<DocumentList> getDocsByTypeAndYear(String type, int year, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getDocumentsByTypeAndYear(type, year, page));
    }

    @Override
    public ResponseEntity<String> getFeedByTypeAndYear(String type, int year, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getFeedByTypeAndYear(type, year, page));
    }
}


