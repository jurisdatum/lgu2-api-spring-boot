package uk.gov.legislation.endpoints.search.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.search.api.SearchApi;
import uk.gov.legislation.endpoints.search.service.SearchService;

import java.io.IOException;

@RestController
public class SearchApiController implements SearchApi {

    private final SearchService searchService;

    public SearchApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity <?> search(String title, int page, String acceptHeader) throws IOException, InterruptedException {
        if(acceptHeader.equals(MediaType.APPLICATION_JSON_VALUE)) {
            return ResponseEntity.ok(searchService.getJsonSearchByTitle(title, page));
        }
        else if(acceptHeader.equals(MediaType.APPLICATION_ATOM_XML_VALUE)) {
            return ResponseEntity.ok(searchService.getAtomSearchByTitle(title, page));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Unsupported format");

    }}
