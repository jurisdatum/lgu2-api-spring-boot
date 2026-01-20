package uk.gov.legislation.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> json(HttpStatusCode status, ErrorResponse body) {
        return ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body);
    }

    private ResponseEntity<ErrorResponse> json(HttpStatusCode status, String message, Exception e) {
        ErrorResponse body = new ErrorResponse(status, message, e.getMessage());
        return json(status, body);
    }

    @ExceptionHandler(UnsupportedLanguageException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedLanguageException(UnsupportedLanguageException ex) {
        return json(HttpStatus.BAD_REQUEST, "Unsupported Language", ex);
    }

    // FixMe this sometimes obscures error unrelated to user input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return json(HttpStatus.BAD_REQUEST, "Invalid Input", ex);
    }

    @ExceptionHandler(NoDocumentException.class)
    public ResponseEntity<ErrorResponse> handleNoDocumentException(NoDocumentException ex) {
        return json(HttpStatus.NOT_FOUND, "Document Not Found", ex);
    }

    @ExceptionHandler(TransformationException.class)
    public ResponseEntity<ErrorResponse> handleAknTransformationException(TransformationException ex) {
        logger.error("TransformationException: {}", ex.getMessage(), ex);
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "Transformation Error", ex);
    }

    @ExceptionHandler(XSLTCompilationException.class)
    public ResponseEntity<ErrorResponse> handleXSLTCompilationException(XSLTCompilationException ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "XSLT Compilation Error", ex);
    }

    @ExceptionHandler(InvalidURISyntaxException.class)
    public ResponseEntity<ErrorResponse> handleInvalidURISyntaxException(InvalidURISyntaxException ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid URI Syntax Error", ex);
    }

    // thrown when MarkLogic returns an error response
    @ExceptionHandler(MarkLogicRequestException.class)
    public ResponseEntity<ErrorResponse> handleMarkLogicRequestException(MarkLogicRequestException ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "MarkLogic Error", ex);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        return json(HttpStatus.SERVICE_UNAVAILABLE, "IO Error", ex);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException ex) {
        Thread.currentThread().interrupt();
        return json(HttpStatus.SERVICE_UNAVAILABLE, "Network Interruption Error", ex);
    }

    // a wrapper around an IOException or an InterruptedException thrown during a request to MarkLogic
    @ExceptionHandler(DocumentFetchException.class)
    public ResponseEntity<ErrorResponse> handleDocumentFetchException(DocumentFetchException ex) {
        return json(HttpStatus.SERVICE_UNAVAILABLE, "Document Fetch Error", ex);
    }

    @ExceptionHandler(UnknownTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnknownTypeException(UnknownTypeException ex) {
        return json(HttpStatus.BAD_REQUEST, "Unknown Document Type Error", ex);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        if (e.getStatusCode().is5xxServerError()) {
            logger.error("ResponseStatusException with 5xx status: {} - {}", e.getStatusCode(), e.getReason(), e);
        }
        ErrorResponse error = new ErrorResponse(
            e.getStatusCode(),
            e.getBody().getTitle(),
            e.getBody().getDetail()
        );
        return json(e.getStatusCode(), error);
    }

}
