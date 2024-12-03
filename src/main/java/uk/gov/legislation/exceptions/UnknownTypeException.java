package uk.gov.legislation.exceptions;


public class UnknownTypeException extends RuntimeException {
    public UnknownTypeException(String type) {
        super("The document type '" + type + "' is not recognized.");
    }
}

