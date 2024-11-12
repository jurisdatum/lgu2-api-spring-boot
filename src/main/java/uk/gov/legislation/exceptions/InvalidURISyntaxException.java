package uk.gov.legislation.exceptions;

public class InvalidURISyntaxException extends RuntimeException{

    public InvalidURISyntaxException(String message, Exception e){
        super(message,e);
    }
}
