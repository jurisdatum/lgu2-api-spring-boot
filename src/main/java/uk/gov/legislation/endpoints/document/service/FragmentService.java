package uk.gov.legislation.endpoints.document.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.util.Constants;

import java.util.Optional;

@Service
public class FragmentService {
    private final Legislation db;

    public FragmentService(Legislation db) {
        this.db = db;
    }

    public Optional <String> getDocumentSection(String type, int year, int number, String section, Optional<String> version) {
            return Optional.ofNullable(db.getDocumentSection(type, year, number, section, version));

    }

    public String getNotFoundMessage(String type, int year, int number) {
        return String.format(Constants.DOCUMENT_NOT_FOUND.getError(), type, year, number);
    }
}

