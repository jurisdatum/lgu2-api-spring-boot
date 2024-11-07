package uk.gov.legislation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.legislation.data.marklogic.NoDocumentException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {
    String isoTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    @ExceptionHandler(NoDocumentException.class)
    public ResponseEntity <ErrorResponse> handleNoDocumentException(NoDocumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("404", ex.getMessage(), isoTimestamp);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}