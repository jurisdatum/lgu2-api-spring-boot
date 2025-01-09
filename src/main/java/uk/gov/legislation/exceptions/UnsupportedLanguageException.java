package uk.gov.legislation.exceptions;

public class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException(String message) {
        super(message);
    }
}

