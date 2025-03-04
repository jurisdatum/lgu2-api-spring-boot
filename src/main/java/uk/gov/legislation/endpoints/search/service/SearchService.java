package uk.gov.legislation.endpoints.search.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import uk.gov.legislation.data.marklogic.search.Search;

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
            String language,
            int page
            ) throws IOException, InterruptedException {
        return db.getAtomByTitleAndTypeAndYearAndNumber(title, type, year, number, language, page);
    }

    public PageOfDocuments getJsonSearchByTitleAndTypeAndYearAndNumber(
            String title,
            String type,
            Integer year,
            Integer number,
            String language,
            int page
            ) throws IOException, InterruptedException {
        SearchResults results = db.getJsonByTitleAndTypeAndYearAndNumber(title, type, year, number, language, page);
        return DocumentsFeedConverter.convert(results);
    }

}
