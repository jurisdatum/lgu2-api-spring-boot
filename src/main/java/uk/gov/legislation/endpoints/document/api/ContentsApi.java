package uk.gov.legislation.endpoints.document.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.TableOfContents;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "Document Contents", description = "API for accessing document contents in various formats")
public interface ContentsApi {

    @Operation(summary = "Retrieve contents in CLML format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contents in CLML format"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/xml")
    ResponseEntity<String> getDocumentContentsClml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException;

    @Operation(summary = "Retrieve contents in AKN format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contents in AKN format"),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "500", description = "Transformation error occurred")
    })
    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentContentsAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException;

    @Operation(summary = "Retrieve contents in JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contents in JSON format"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException;
}
