package uk.gov.legislation.endpoints.ld.legislature;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

@Tag(name = "Linked Data")
@RequestMapping("/ld/legislature")
public interface LegislatureApi {


    @GetMapping(path = "/{name}", produces = {
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
     ResponseEntity <?> getCalendar(NativeWebRequest request,
        @PathVariable String name) throws Exception;
}
