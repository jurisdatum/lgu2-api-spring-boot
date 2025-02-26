package uk.gov.legislation.endpoints.types;

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
import uk.gov.legislation.api.responses.TypeWrapper;
import uk.gov.legislation.api.responses.TypesForCountry;
import uk.gov.legislation.exceptions.ErrorResponse;

import java.util.List;

@Tag(name = "Document types", description = "APIs for fetching document types and their country-specific applicability")
public interface TypesApi {

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch all document types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched document types",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = TypeWrapper.class)))
    })
    ResponseEntity<List<TypeWrapper>> getAllTypes();

    @GetMapping(value = "/types/{country}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch document types related to a specific country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched country-specific document types",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TypesForCountry.class))),
            @ApiResponse(responseCode = "400", description = "Unrecognized country",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<TypesForCountry> getTypesForCountry(@PathVariable String country);

}
