package uk.gov.legislation.endpoints.ld.monarch;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;

@Tag(name = "Linked Data", description = "APIs for fetching information about a specific monarch")
public interface MonarchApi {

    @GetMapping(
        path = "/ld/monarch/{monarch}",
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
        }
    )
    ResponseEntity<?> getMonarchInfo(
        NativeWebRequest request,
        @Parameter(description = "Monarch", example = "Eliz1")
        @PathVariable String monarch
    ) throws Exception;
}

