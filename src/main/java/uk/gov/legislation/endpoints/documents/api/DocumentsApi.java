package uk.gov.legislation.endpoints.documents.api;

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
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.exceptions.ErrorResponse;

import java.io.IOException;

@Tag(name = "Document lists", description = "APIs for fetching document lists and feeds")
public interface DocumentsApi {

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch documents by type in Atom or JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched document list",
                    content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DocumentList.class)),
                        @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE)
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid document type",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DocumentList> getDocs(
            @PathVariable String type,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> getFeed(
            @PathVariable String type,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch documents by type and year in Atom or JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched document list",
                    content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DocumentList.class)),
                        @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE)
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid document type",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DocumentList> getDocsByTypeAndYear(
            @PathVariable String type,
            @PathVariable int year,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) throws IOException, InterruptedException;

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> getFeedByTypeAndYear(
            @PathVariable String type,
            @PathVariable int year,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) throws IOException, InterruptedException;

}