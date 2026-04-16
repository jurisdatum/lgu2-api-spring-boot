package uk.gov.legislation.exceptions;

public class MarkLogicRequestException extends RuntimeException{
    public MarkLogicRequestException(String message){
        super(message);
    }
    public MarkLogicRequestException(String message, Throwable cause){
        super(message, cause);
    }
}
