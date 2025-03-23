package uk.gov.legislation.endpoints.ld.sparql;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Linked Data")
public interface SparqlApi {

    @RequestMapping(
            value = "/ld/sparql",
            method = { RequestMethod.GET, RequestMethod.POST },
            produces = {
                    "application/json",
                    "application/rdf+xml",
                    "application/sparql-results+json",
                    "application/sparql-results+xml",
                    "application/xml",
                    "text/csv",
                    "text/plain",
                    "text/turtle"
            }
    )
    ResponseEntity<String> sparql(
            @RequestParam
            @Parameter(
                    description = "SPARQL query",
                    example = """
                            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                            PREFIX leg: <http://www.legislation.gov.uk/def/legislation/>

                            SELECT * WHERE {
                               ?s ?p ?o
                            }
                            LIMIT 10
                            """
            )
            String query,
            @RequestHeader(value = "Accept") String accept
    ) throws Exception;

}
