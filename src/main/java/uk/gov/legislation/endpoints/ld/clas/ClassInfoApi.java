package uk.gov.legislation.endpoints.ld.clas;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Linked Data", description = "APIs for fetching class information")
public interface ClassInfoApi {

    @GetMapping(value = "/ld/class/{name}",
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
    ResponseEntity<String> getClassInfo(
            @PathVariable
            @Parameter(description = "Type of class", example = "Item") String name,
            @RequestHeader(value = "Accept") String accept
    ) throws Exception;

}
