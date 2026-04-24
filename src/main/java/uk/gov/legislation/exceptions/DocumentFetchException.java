package uk.gov.legislation.exceptions;

public  class DocumentFetchException extends RuntimeException {
    public DocumentFetchException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentFetchException(String message) {
        super(message);
    }
}
