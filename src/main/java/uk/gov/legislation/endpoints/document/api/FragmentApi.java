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
import uk.gov.legislation.exceptions.TransformationException;

import java.io.IOException;
import java.util.Optional;

/**
 * API interface for accessing document fragments in various formats.
 */
@Tag(name = "Fragment-Documents", description = "API for accessing document fragments in different formats")

public interface FragmentApi {

    @Operation(summary = "Retrieve document fragment in CLML format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved CLML document fragment"),
            @ApiResponse(responseCode = "404", description = "Document fragment not found")
    })
    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/xml")
    ResponseEntity <String> getFragmentClml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional <String> version) throws IOException, InterruptedException, NoDocumentException;

    @Operation(summary = "Retrieve document fragment in AKN format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved AKN document fragment"),
            @ApiResponse(responseCode = "404", description = "Document fragment not found")
    })
    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/akn+xml")
    ResponseEntity<String> getFragmentAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException, TransformationException;

    @Operation(summary = "Retrieve document fragment in HTML format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved HTML document fragment"),
            @ApiResponse(responseCode = "404", description = "Document fragment not found")
    })
    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "text/html")
    ResponseEntity<String> getFragmentHtml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException, TransformationException;

    @Operation(summary = "Retrieve document fragment in JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved JSON document fragment"),
            @ApiResponse(responseCode = "404", description = "Document fragment not found")
    })
    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/json")
    ResponseEntity<DocumentApi.Response> getFragmentJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version) throws IOException, InterruptedException, NoDocumentException, TransformationException;
}
