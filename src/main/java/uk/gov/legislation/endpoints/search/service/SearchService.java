package uk.gov.legislation.endpoints.search.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.data.marklogic.queries.Search;
import uk.gov.legislation.endpoints.documents.Converter;
import uk.gov.legislation.endpoints.documents.DocumentList;

import java.io.IOException;

@Service
public class SearchService {

    private final Search db;

    public SearchService(Search db) {
        this.db = db;
    }

    public String getAtomSearchByTitleAndTypeAndYearAndNumber(
            String title,
            String type,
            Integer year,
            Integer number,
            int page) throws IOException, InterruptedException {
        return db.getAtomByTitleAndTypeAndYearAndNumber(title, type, year, number, page);
    }

    public DocumentList getJsonSearchByTitleAndTypeAndYearAndNumber(
            String title,
            String type,
            Integer year,
            Integer number,
            int page) throws IOException, InterruptedException {
        SearchResults results = db.getJsonByTitleAndTypeAndYearAndNumber(title, type, year, number, page);
        return Converter.convert(results);
    }

}
