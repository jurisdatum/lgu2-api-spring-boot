package uk.gov.legislation.endpoints.ld.items;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.parameters.Type;

@Tag(name = "Linked Data")
@RequestMapping("/ld/items")
public interface ItemsApi {

    @GetMapping(path = {"/{type}", "/{type}/{year}"}, produces = {
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
     ResponseEntity <?> typeAndYear(NativeWebRequest request,
        @PathVariable @Type String type,
        @PathVariable(required = false) @Number Integer year,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize) throws Exception;
}
