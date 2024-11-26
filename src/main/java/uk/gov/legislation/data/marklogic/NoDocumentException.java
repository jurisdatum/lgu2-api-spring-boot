package uk.gov.legislation.data.marklogic;

public class NoDocumentException extends RuntimeException {

    public NoDocumentException(String message) {
        super(message);
    }

    NoDocumentException(Error error) {
        this(error.message);
    }

}
