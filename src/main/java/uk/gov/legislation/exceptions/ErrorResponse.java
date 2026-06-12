package uk.gov.legislation.exceptions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatusCode;

public class ErrorResponse {

    public final int status;
    public final String error;
    public final String message;
    public final String timestamp;

    ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp =
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    ErrorResponse(HttpStatusCode status, String error, String message) {
        this(status.value(), error, message);
    }
}
