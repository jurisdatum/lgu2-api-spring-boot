package uk.gov.legislation.endpoints.metadata.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.data.virtuoso.model.MetadataItem;

import java.io.IOException;
import java.util.List;

@Tag(name = "Linked Data", description = "APIs for fetching metadata information")
public interface MetadataApi {

    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Item> getMetadata(
            @PathVariable
            @Parameter(description = "Type of ACT", example = "ukpga")
            String type,
            @PathVariable
            @Parameter(description = "Year of publication", example = "2023")
            int year,
            @PathVariable
            @Parameter(description = "Number", example = "1")
            int number
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/metadata/{type}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MetadataItem>> getMetadataList(
            @PathVariable
            @Parameter(description = "Type of ACT", example = "ukpga")
            String type,
            @PathVariable
            @Parameter(description = "Year of publication", example = "2023")
            int year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) throws IOException, InterruptedException;

}
