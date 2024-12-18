package uk.gov.legislation.exceptions;

import uk.gov.legislation.data.marklogic.Error;

public class NoDocumentException extends RuntimeException {

    private NoDocumentException(String message) {
        super(message);
    }

    public NoDocumentException(Error error) {
        this(error.message);
    }

}
