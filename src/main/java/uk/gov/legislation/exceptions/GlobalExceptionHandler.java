package uk.gov.legislation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.legislation.data.marklogic.NoDocumentException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoDocumentException.class)
    public ResponseEntity <ErrorResponse> handleNoDocumentException(NoDocumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Not Found",
                "Document not found: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransformationException.class)
    public ResponseEntity<ErrorResponse> handleAknTransformationException(TransformationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Transformation Error",
                "Transformation failed: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(XSLTCompilationException.class)
    public ResponseEntity<ErrorResponse> handleXSLTCompilationException(XSLTCompilationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Error compiling XSLT",
                "XSLTCompilationException failed: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidURISyntaxException.class)
    public ResponseEntity<ErrorResponse> handleInvalidURISyntaxException(InvalidURISyntaxException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "InvalidURISyntax_Error",
                "InvalidURISyntax: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MarkLogicRequestException.class)
    public ResponseEntity<ErrorResponse> handleMarkLogicRequestException(MarkLogicRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "MarkLogic Request Error",
                "MarkLogic Error: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "IOException_Error",
                "An IO error occurred: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException ex) {
        Thread.currentThread().interrupt();
        ErrorResponse errorResponse = new ErrorResponse(
                "InterruptedException_Error",
                "The request was interrupted: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(DocumentFetchException.class)
    public ResponseEntity<Object> handleDocumentFetchException(DocumentFetchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Document fetch failed",
                "Document fetch failed: " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
