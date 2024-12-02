package uk.gov.legislation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public  class UnknownTypeException extends ResponseStatusException {
    public UnknownTypeException(String type) {
        super(HttpStatus.BAD_REQUEST, "Unknown document type: " + type);
    }
}
