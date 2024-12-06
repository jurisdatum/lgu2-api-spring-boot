package uk.gov.legislation.endpoints.metadata.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.exceptions.ErrorResponse;

import java.io.IOException;

@Tag(name = "Linked Data", description = "APIs for fetching metadata information")
public interface MetadataApi {

    @GetMapping(value = "/metadata/{type}/{year}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch metadata by type, year, and number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched metadata",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "404", description = "Metadata not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity <Item> getMetadata(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number
    ) throws IOException, InterruptedException;
}

