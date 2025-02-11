package uk.gov.legislation.endpoints.types.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.exceptions.ErrorResponse;

import java.util.List;

@Tag(name = "Document types", description = "APIs for fetching document types and their country-specific applicability")
public interface TypesApi {

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch all document types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched document types",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TypeWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity <List <TypeWrapper>> getAllTypes();

    @GetMapping(value = "/types/uk", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch document types specific to the UK")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched UK-specific document types",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TypesForCountry.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<TypesForCountry> getUkSpecificTypes();
}

