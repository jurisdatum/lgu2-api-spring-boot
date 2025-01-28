package uk.gov.legislation.endpoints.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.endpoints.search.api.SearchApi;
import uk.gov.legislation.endpoints.search.service.SearchService;

import java.io.IOException;

import static uk.gov.legislation.endpoints.documents.service.DocumentsService.validateType;

@RestController
public class SearchApiController implements SearchApi {

    private final SearchService searchService;

    public SearchApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity<PageOfDocuments> searchByJson(
            String title,
            String type,
            Integer year,
            Integer number,
            int page) throws IOException, InterruptedException {
        if (title != null && title.isBlank())
            title = null;
        if (type != null && type.isBlank())
            type = null;
        if (type != null)
            validateType(type);
        return ResponseEntity.ok(searchService.getJsonSearchByTitleAndTypeAndYearAndNumber(title,type,year,number, page));
    }

    @Override
    public ResponseEntity<String> searchByAtom(
            String title,
            String type,
            Integer year,
            Integer number,
            int page) throws IOException, InterruptedException {
        if (title != null && title.isBlank())
            title = null;
        if (type != null && type.isBlank())
            type = null;
        if (type != null)
            validateType(type);
        return ResponseEntity.ok(searchService.getAtomSearchByTitleAndTypeAndYearAndNumber(title,type,year,number, page));
    }

}
