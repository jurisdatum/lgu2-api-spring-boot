package uk.gov.legislation.data.marklogic;

public class NoDocumentException extends Exception {

    NoDocumentException(Error error) {
        super(error.message);
    }

}
