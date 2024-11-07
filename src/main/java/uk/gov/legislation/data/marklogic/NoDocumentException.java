package uk.gov.legislation.data.marklogic;

public class NoDocumentException extends Exception {

    public NoDocumentException(String error) {
        super(error);
    }

}
