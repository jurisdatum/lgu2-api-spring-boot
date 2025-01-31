package uk.gov.legislation.endpoints.documents.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import java.io.IOException;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@Service
public class DocumentsService {

    private final Search db;

    public DocumentsService(Search db) {
        this.db = db;
    }

    public PageOfDocuments getDocumentsByType(String type, int page) throws IOException, InterruptedException {
        validateType(type);
        SearchResults results = db.byType(type, page);
        return DocumentsFeedConverter.convert(results);
    }

    public String getFeedByType(String type, int page) throws IOException, InterruptedException {
        validateType(type);
        return db.byTypeAtom(type, page);
    }

    public PageOfDocuments getDocumentsByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        validateType(type);
        SearchResults results = db.byTypeAndYear(type, year, page);
        return DocumentsFeedConverter.convert(results);
    }

    public String getFeedByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        validateType(type);
        return db.byTypeAndYearAtom(type, year, page);
    }



}
