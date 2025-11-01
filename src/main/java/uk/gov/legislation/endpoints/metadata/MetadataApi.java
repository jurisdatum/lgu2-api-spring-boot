package uk.gov.legislation.endpoints.metadata;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.legislation.api.responses.ExtendedMetadata;

import java.util.Locale;

public interface MetadataApi {

    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = "application/xml")
    ResponseEntity<String> xml(
        @PathVariable String type,
        @PathVariable String year,
        @PathVariable int number,
        Locale locale
    ) throws Exception;

    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = "application/json")
    ResponseEntity<ExtendedMetadata> json(
        @PathVariable String type,
        @PathVariable String year,
        @PathVariable int number,
        Locale locale
    ) throws Exception;

}
