package uk.gov.legislation.endpoints.search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.endpoints.documents.DocumentList;
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
    public ResponseEntity <DocumentList> searchJson(String title, int page) throws IOException, InterruptedException {
            return ResponseEntity.ok(searchService.getJsonSearchByTitle(title, page));
        }

    @Override
    public ResponseEntity <String> searchAtom(String title, int page) throws IOException, InterruptedException {
        return ResponseEntity.ok(searchService.getAtomSearchByTitle(title, page));
    }

    @Override
    public ResponseEntity <DocumentList> searchByJson(
            String title,
            String type,
            String year,
            String number,
            int page) throws IOException, InterruptedException {
        validateType(type);
            return ResponseEntity.ok(searchService.getJsonSearchByTitleAndTypeAndYearAndNumber(title,type,year,number, page));
        }

    @Override
    public ResponseEntity <String> searchByAtom(
            String title,
            String type,
            String year,
            String number,
            int page) throws IOException, InterruptedException {
        validateType(type);
        return ResponseEntity.ok(searchService.getAtomSearchByTitleAndTypeAndYearAndNumber(title,type,year,number, page));

    }
}


