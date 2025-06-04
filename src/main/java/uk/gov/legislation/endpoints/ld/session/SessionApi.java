package uk.gov.legislation.endpoints.ld.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@Tag(name = "Linked Data")

@RequestMapping(
    path = "/ld/session",
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
public interface SessionApi {

    @GetMapping("/{legislature}/{reign}/{session}")
    @Operation(summary = "information about a parliamentary session")
    ResponseEntity<?> getSessionByLegislatureReign(
        NativeWebRequest request,
        @Parameter(description = "Legislature", example = "Eliz1 ")
        @PathVariable String legislature,
        @Parameter(description = "Reign", example = "Eliz1")
        @PathVariable String reign,
        @Parameter(description = "Session", example = "tempincert")
        @PathVariable String session
    ) throws Exception;

    @GetMapping("/EnglishParliament/{session}")
    @Operation(summary = "information about a session of the English parliament")
    ResponseEntity<?> getEnglishParliamentSession(
        NativeWebRequest request,
        @Parameter(description = "Session", example = "tempincert")
        @PathVariable String session
    ) throws Exception;

}
