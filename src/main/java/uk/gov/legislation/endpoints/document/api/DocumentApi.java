package uk.gov.legislation.endpoints.document.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.Metadata;

import java.io.IOException;
import java.util.Optional;

/**
 * API for document retrieval and transformation operations.
 */
@Tag(name = "Documents", description = "Operations related to individual document retrieval and transformation")
public interface DocumentApi {

    /**
     * Retrieves document content in CLML format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/xml")
    @Operation(
            summary = "Retrieve document content in CLML format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Retrieve document content in CLML format",
                            content = @Content(schema = @Schema(implementation = uk.gov.legislation.endpoints.types.Legislation.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Document not found")
            }
    )
    ResponseEntity<String> getDocumentClml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version
    ) throws IOException, InterruptedException, NoDocumentException;

    /**
     * Retrieves document content in AKN format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/akn+xml")
    @Operation(
            summary = "Retrieve document content in AKN format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(responseCode = "404", description = "Document not found")
            }
    )
    ResponseEntity<String> getDocumentAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version
    ) throws Exception;

    /**
     * Retrieves document content in HTML format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "text/html")
    @Operation(
            summary = "Retrieve document content in HTML format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(responseCode = "404", description = "Document not found")
            }
    )
    ResponseEntity<String> getDocumentHtml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version
    ) throws Exception;

    /**
     * Record representing the response structure for JSON document content.
     */
    record Response(Metadata meta, String html) {}

    /**
     * Retrieves document metadata and HTML content in JSON format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/json")
    @Operation(
            summary = "Retrieve document metadata and HTML content in JSON format",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Response.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Document not found")
            }
    )
    ResponseEntity<Response> getDocumentJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version
    ) throws Exception;
}
