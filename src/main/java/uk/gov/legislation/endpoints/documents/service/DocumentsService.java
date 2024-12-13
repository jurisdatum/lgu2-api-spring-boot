package uk.gov.legislation.endpoints.documents.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.queries.Search;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.endpoints.documents.Converter;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.util.Types;

import java.io.IOException;

@Service
public class DocumentsService {

    private final Search db;

    public DocumentsService(Search db) {
        this.db = db;
    }

    public DocumentList getDocumentsByType(String type, int page) throws IOException, InterruptedException {
        validateType(type);
        SearchResults results = db.byType(type, page);
        return Converter.convert(results);
    }

    public String getFeedByType(String type, int page) throws IOException, InterruptedException {
        validateType(type);
        return db.byTypeAtom(type, page);
    }

    public DocumentList getDocumentsByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        validateType(type);
        SearchResults results = db.byTypeAndYear(type, year, page);
        return Converter.convert(results);
    }

    public String getFeedByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        validateType(type);
        return db.byTypeAndYearAtom(type, year, page);
    }

    private void validateType(String type) {
        if (!Types.isValidShortType(type)) {
            throw new UnknownTypeException(type);
        }
    }

}
