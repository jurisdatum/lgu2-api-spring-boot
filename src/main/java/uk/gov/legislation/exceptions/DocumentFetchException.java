package uk.gov.legislation.exceptions;

// Custom exception for document fetch issues
public  class DocumentFetchException extends RuntimeException {
    public DocumentFetchException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocumentFetchException(String message) {
        super(message);
    }
}