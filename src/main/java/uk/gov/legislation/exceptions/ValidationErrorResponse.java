package uk.gov.legislation.exceptions;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {

    public final Map<String, String> errors;

    ValidationErrorResponse(int status, String error, String message, Map<String, String> errors) {
        super(status, error, message);
        this.errors = errors;
    }

}
