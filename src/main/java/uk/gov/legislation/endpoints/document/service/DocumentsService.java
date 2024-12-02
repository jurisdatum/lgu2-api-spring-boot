package uk.gov.legislation.endpoints.document.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Search;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.endpoints.documents.Converter;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.util.Types;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentsService {

    private final Search db;


    public DocumentsService(Search db ) {
        this.db = db;

    }

    public Optional<DocumentList> getDocumentsByType(String type, int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type)) {
            return Optional.empty();
        }
        SearchResults results = db.byType(type, page);
        return Optional.of(Converter.convert(results));
    }

    public Optional<String> getFeedByType(String type, int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type)) {
            return Optional.empty();
        }
        return Optional.of(db.byTypeAtom(type, page));
    }

    public Optional<DocumentList> getDocumentsByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type)) {
            return Optional.empty();
        }
        SearchResults results = db.byTypeAndYear(type, year, page);
        return Optional.of(Converter.convert(results));
    }

    public Optional<String> getFeedByTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type)) {
            return Optional.empty();
        }
        return Optional.of(db.byTypeAndYearAtom(type, year, page));
    }
}

