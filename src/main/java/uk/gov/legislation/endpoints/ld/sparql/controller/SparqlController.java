package uk.gov.legislation.endpoints.ld.sparql.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.endpoints.ld.sparql.api.SparqlApi;

@RestController
public class SparqlController implements SparqlApi {

    private final Virtuoso virtuoso;

    public SparqlController(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    @Override
    public ResponseEntity<String> sparql(String query, String accept) throws Exception {
        String format = switch (accept) {
            case "application/rdf+xml", "application/sparql-results+json",
                 "application/sparql-results+xml",
                 "text/csv", "text/plain", "text/turtle" -> accept;
            case "application/xml" -> "application/sparql-results+xml";
            default -> "application/sparql-results+json";
        };
        String response;
        try {
            response = virtuoso.query(query, format);
        } catch (IllegalArgumentException e) {
            throw new HttpMediaTypeNotAcceptableException(format);
        }
        return ResponseEntity.ok().body(response);
    }

}
