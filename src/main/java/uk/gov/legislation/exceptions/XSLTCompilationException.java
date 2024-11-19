package uk.gov.legislation.exceptions;

public class XSLTCompilationException extends RuntimeException{
    public XSLTCompilationException(String message){
        super(message);
    }

    public XSLTCompilationException(String failedToCompileXslt, Exception e) {
    }
}
