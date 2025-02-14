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
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.legislation.endpoints.types.TypeWrapper;
import uk.gov.legislation.endpoints.types.TypesForCountry;
import uk.gov.legislation.exceptions.ErrorResponse;

import java.util.List;

@Tag(name = "Document types", description = "APIs for fetching document types and their country-specific applicability")
@RequestMapping("/types")
public interface TypesApi {

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/uk", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/wales", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch document types specific to Wales")
    ResponseEntity<TypesForCountry> getWalesSpecificTypes();

    @GetMapping(value = "/scotland", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch document types specific to Scotland")
    ResponseEntity<TypesForCountry> getScotlandSpecificTypes();

    @GetMapping(value = "/northern-ireland", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch document types specific to Northern Ireland")
    ResponseEntity<TypesForCountry> getNorthernIrelandSpecificTypes();
}


