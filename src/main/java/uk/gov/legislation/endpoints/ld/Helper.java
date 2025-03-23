package uk.gov.legislation.endpoints.ld;

import org.springframework.web.HttpMediaTypeNotAcceptableException;

public class Helper {

    public static String getFormat(String accept) throws HttpMediaTypeNotAcceptableException {
        if (accept == null)
            return "application/sparql-results+json";
        return switch (accept) {
            case "*/*", "application/sparql-results+json", "application/json" -> "application/sparql-results+json";
            case "application/sparql-results+xml", "application/xml" -> "application/sparql-results+xml";
            case "application/rdf+xml" -> "application/rdf+xml";
            case "text/csv" -> "text/csv";
            case "text/plain" -> "text/plain";
            case "text/turtle" -> "text/turtle";
            default -> throw new HttpMediaTypeNotAcceptableException(accept);
        };
    }

}
