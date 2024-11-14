package uk.gov.legislation.endpoints.document.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;

import java.io.IOException;
import java.util.Optional;

@Service
public class FragmentService {
    private final Legislation db;

    private static final String DOCUMENT_NOT_FOUND_MESSAGE = "Document not found for type: %s, year: %d, number: %d";

    public FragmentService(Legislation db) {
        this.db = db;
    }

    public Optional <String> getDocumentSection(String type, int year, int number, String section, Optional<String> version)
            throws IOException, InterruptedException, NoDocumentException {
            return Optional.ofNullable(db.getDocumentSection(type, year, number, section, version));

    }

    public String getNotFoundMessage(String type, int year, int number) {
        return String.format(DOCUMENT_NOT_FOUND_MESSAGE, type, year, number);
    }
}

