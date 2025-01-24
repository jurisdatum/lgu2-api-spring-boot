package uk.gov.legislation.endpoints.documents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.endpoints.documents.api.DocumentsApi;
import uk.gov.legislation.endpoints.documents.service.DocumentsService;

import java.io.IOException;

@RestController
public class DocumentsApiController implements DocumentsApi {

    private final DocumentsService documentsService;

    public DocumentsApiController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocs(String type, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getDocumentsByType(type, page));
    }

    @Override
    public ResponseEntity<String> getFeed(String type, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getFeedByType(type, page));
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocsByTypeAndYear(String type, int year, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getDocumentsByTypeAndYear(type, year, page));
    }

    @Override
    public ResponseEntity<String> getFeedByTypeAndYear(String type, int year, int page)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(documentsService.getFeedByTypeAndYear(type, year, page));
    }
}


