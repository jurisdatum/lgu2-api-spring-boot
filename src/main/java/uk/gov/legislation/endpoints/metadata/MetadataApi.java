package uk.gov.legislation.endpoints.metadata;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.parameters.Type;
import uk.gov.legislation.api.parameters.Year;
import uk.gov.legislation.api.responses.ExtendedMetadata;

import java.util.Locale;

public interface MetadataApi {

    @Operation(
        summary = "Fetch CLML metadata",
        description = "Streams the CLML metadata. " +
            "See schema at https://www.legislation.gov.uk/schema/schemaLegislationMetadata.xsd."
    )
    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = "application/xml")
    ResponseEntity<String> xml(
        @PathVariable @Type String type,
        @PathVariable @Year String year,
        @PathVariable @Number int number,
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
