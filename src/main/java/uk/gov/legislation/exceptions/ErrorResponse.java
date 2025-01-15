package uk.gov.legislation.exceptions;

import org.springframework.http.HttpStatusCode;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    ErrorResponse(HttpStatusCode status, String error, String message) {
        this(status.value(), error, message);
    }

}
