package uk.gov.legislation.endpoints.ld.regnal;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.parameters.RegnalYear;
import uk.gov.legislation.api.parameters.Reign;

@Tag(name = "Linked Data", description = "endpoint for fetching information about a specific regnal year within a " +
    "reign")
public interface RegnalYearApi {

    @GetMapping(path = "/ld/regnal/{reign}/{regnalYear}",
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
    ResponseEntity <?> getRegnalYearInfo(
        NativeWebRequest request,
        @PathVariable @Reign String reign,
        @PathVariable @RegnalYear Integer regnalYear
    ) throws Exception;

}
