package uk.gov.legislation.exceptions;

import org.springframework.http.HttpStatusCode;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {

    public final int status;
    public final String error;
    public final String message;
    public final String timestamp;

    ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    ErrorResponse(HttpStatusCode status, String error, String message) {
        this(status.value(), error, message);
    }

}
