package uk.gov.legislation.exceptions;

// Custom exception for document fetch issues
public  class DocumentFetchException extends RuntimeException {
    public DocumentFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}