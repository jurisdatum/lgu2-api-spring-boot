package uk.gov.legislation.endpoints.ld.sparql;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.parameters.SparqlQuery;

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
            @RequestParam @SparqlQuery String query,
            @RequestHeader(value = "Accept")
            @Parameter(hidden = true)String accept
    ) throws Exception;

}
