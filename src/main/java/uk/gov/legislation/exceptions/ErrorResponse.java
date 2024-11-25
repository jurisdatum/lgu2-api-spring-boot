package uk.gov.legislation.exceptions;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ErrorResponse {

    private final String status;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
