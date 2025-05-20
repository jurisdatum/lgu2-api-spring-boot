package uk.gov.legislation.endpoints.ld.reign.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;

@Tag(name = "Linked Data", description = "endpoint for fetching information about a specific reign")
public interface ReignApi {

    @GetMapping(path = "/ld/reign/{id}",
        produces = {
            "application/xml",
            "application/json",
            "application/rdf+xml",
            "application/rdf+json",
            "application/ld+json",
            "application/sparql-results+json",
            "application/sparql-results+xml",
            "text/csv",
            "text/plain",
            "text/turtle"
        })
    ResponseEntity<?> get(
            NativeWebRequest request,
            @Parameter(description = "The id of the reign", example = "WillandMar")
            @PathVariable String id
    ) throws Exception;

}
