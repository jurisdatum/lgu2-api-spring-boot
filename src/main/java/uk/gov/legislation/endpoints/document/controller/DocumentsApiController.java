package uk.gov.legislation.endpoints.document.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.document.api.DocumentsApi;
import uk.gov.legislation.endpoints.document.service.DocumentsService;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.exceptions.UnknownTypeException;

import java.io.IOException;

@RestController
public class DocumentsApiController implements DocumentsApi {

    private final DocumentsService documentsService;

    public DocumentsApiController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @Override
    public ResponseEntity<DocumentList> getDocs(String type, int page) throws IOException, InterruptedException {
        return documentsService.getDocumentsByType(type, page)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UnknownTypeException(type));
    }

    @Override
    public ResponseEntity<String> getFeed(String type, int page) throws IOException, InterruptedException {
        return documentsService.getFeedByType(type, page)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UnknownTypeException(type));
    }

    @Override
    public ResponseEntity<DocumentList> getDocsByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        return documentsService.getDocumentsByTypeAndYear(type, year, page)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UnknownTypeException(type));
    }

    @Override
    public ResponseEntity<String> getFeedByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        return documentsService.getFeedByTypeAndYear(type, year, page)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UnknownTypeException(type));
    }


    }


