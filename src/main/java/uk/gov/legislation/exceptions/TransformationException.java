package uk.gov.legislation.exceptions;

public class TransformationException extends RuntimeException{

    public TransformationException(String message, Exception ex){
        super(message, ex);
    }
}
