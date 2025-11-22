package uk.gov.legislation.endpoints.ld.interpretation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.parameters.Type;
import uk.gov.legislation.api.parameters.Version;
import uk.gov.legislation.api.parameters.Year;

import java.util.Locale;
@Tag(name = "Linked Data")
@RequestMapping("/ld/interpretation")
public interface InterpretationApi {


    @GetMapping(path = "/{type}/{year:\\d{4}}/{number}", produces = {
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
     ResponseEntity <?> getCalendarYearAndNumber(NativeWebRequest request,
        @PathVariable @Type String type,
        @PathVariable @Year int year,
        @PathVariable @Number int number,
        @RequestParam(required = false) @Version String version,
        Locale locale) throws Exception;
}
