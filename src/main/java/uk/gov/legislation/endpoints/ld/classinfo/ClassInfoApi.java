package uk.gov.legislation.endpoints.ld.classinfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.parameters.Name;

@Tag(name = "Linked Data", description = "APIs for fetching class information")
public interface ClassInfoApi {

    @GetMapping(
        value = "/ld/class/{name}",
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
        }
    )
    @Operation(summary = "information about a class")
    ResponseEntity<?> getClassInfo(NativeWebRequest request,
        @PathVariable @Name String name) throws Exception;

}
