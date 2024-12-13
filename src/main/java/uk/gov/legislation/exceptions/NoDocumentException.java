package uk.gov.legislation.exceptions;

import uk.gov.legislation.data.marklogic.Error;

public class NoDocumentException extends RuntimeException {

    public NoDocumentException(String message) {
        super(message);
    }

    NoDocumentException(Error error) {
        this(error.message);
    }

}
