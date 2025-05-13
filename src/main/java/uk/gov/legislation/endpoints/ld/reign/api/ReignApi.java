package uk.gov.legislation.endpoints.ld.reign.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;

@Tag(name = "Linked Data", description = "APIs for fetching information about a specific reign")
public interface ReignApi {

    @GetMapping(path = "/ld/reign/{reign}",
        produces = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE,
            "application/rdf+xml",
            "application/rdf+json",
            "application/ld+json",
            "application/sparql-results+json",
            "application/sparql-results+xml",
            "text/csv",
            "text/plain",
            "text/turtle"
        })
    ResponseEntity <?> getReignInfo(
            NativeWebRequest request,
            @Parameter(description = "Reign", example = "Eliz1")
            @PathVariable String reign
    ) throws Exception;
}

