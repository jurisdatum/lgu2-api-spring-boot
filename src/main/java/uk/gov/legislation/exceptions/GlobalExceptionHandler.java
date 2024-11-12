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
        ErrorResponse errorResponse = new ErrorResponse(
                "Not Found",
                "Document not found: " + ex.getMessage(),
                isoTimestamp
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(TransformationException.class)
    public ResponseEntity<ErrorResponse> handleAknTransformationException(TransformationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Transformation Error",
                "Transformation failed: " + ex.getMessage(),
                isoTimestamp
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(XSLTCompilationException.class)
    public ResponseEntity<ErrorResponse> handleXSLTCompilationException(XSLTCompilationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Error compiling XSLT",
                "XSLTCompilationException failed: " + ex.getMessage(),
                isoTimestamp
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidURISyntaxException.class)
    public ResponseEntity<ErrorResponse> handleInvalidURISyntaxException(InvalidURISyntaxException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "InvalidURISyntax_Error",
                "InvalidURISyntax: " + ex.getMessage(),
                isoTimestamp
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MarkLogicRequestException.class)
    public ResponseEntity<ErrorResponse> handleMarkLogicRequestException(MarkLogicRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Mark-Logic Request Error",
                "Mark-Logic Error: " + ex.getMessage(),
                isoTimestamp
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}