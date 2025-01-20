package uk.gov.legislation.endpoints.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.documents.DocumentList;
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
    public ResponseEntity <DocumentList> search(String title, int page) throws IOException, InterruptedException {
        return ResponseEntity.ok(searchService.getSearchByTitle(title, page));

    }
}
