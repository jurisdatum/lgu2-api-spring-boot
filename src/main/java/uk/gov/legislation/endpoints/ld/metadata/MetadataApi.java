package uk.gov.legislation.endpoints.ld.metadata;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Linked Data", description = "APIs for fetching metadata information")
public interface MetadataApi {

    @GetMapping(
            value = "/metadata/{type}/{year}/{number}",
            produces = {
                    "application/json",
                    "application/rdf+xml",
                    "application/sparql-results+json",
                    "application/sparql-results+xml",
                    "application/xml",
                    "text/csv",
                    "text/plain",
                    "text/turtle"
            }
    )
    ResponseEntity<String> getMetadata(
            @PathVariable
            @Parameter(description = "Type of ACT", example = "ukpga")
            String type,
            @PathVariable
            @Parameter(description = "Year of publication", example = "2023")
            int year,
            @PathVariable
            @Parameter(description = "Number", example = "1")
            int number,
            @RequestHeader(value = "Accept")
            @Parameter(hidden = true)String accept
    ) throws Exception;

    @GetMapping(
            value = "/metadata/{type}/{year}",
            produces = {
                    "application/json",
                    "application/rdf+xml",
                    "application/sparql-results+json",
                    "application/sparql-results+xml",
                    "application/xml",
                    "text/csv",
                    "text/plain",
                    "text/turtle"
            }
    )
    ResponseEntity<String> getMetadataList(
            @PathVariable
            @Parameter(description = "Type of ACT", example = "ukpga")
            String type,
            @PathVariable
            @Parameter(description = "Year of publication", example = "2023")
            int year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestHeader(value = "Accept")
            @Parameter(hidden = true)String accept
    ) throws Exception;

}
