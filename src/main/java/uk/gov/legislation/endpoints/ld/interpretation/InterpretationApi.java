package uk.gov.legislation.endpoints.ld.interpretation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

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
        @PathVariable String type,
        @PathVariable int year,
        @PathVariable int number,
        @RequestParam(required = false) String version,
        Locale locale) throws Exception;
}
