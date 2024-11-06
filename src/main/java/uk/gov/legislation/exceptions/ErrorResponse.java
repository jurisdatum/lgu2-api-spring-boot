package uk.gov.legislation.exceptions;


public class ErrorResponse {
    private final String status;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}