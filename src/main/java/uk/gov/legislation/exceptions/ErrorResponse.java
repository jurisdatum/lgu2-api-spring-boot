package uk.gov.legislation.exceptions;


import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String status;
    private final String message;
    private final String timestamp;

    public ErrorResponse(String status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }


}
