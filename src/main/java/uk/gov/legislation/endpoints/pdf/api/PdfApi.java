package uk.gov.legislation.endpoints.pdf.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@Tag(name = "PDFs")
public interface PdfApi {

    /**
     * Get the PDF version of a document.
     *
     * @param type    Document type identifier
     * @param year    Publication year
     * @param number  Document number
     * @param version Optional version of the document
     * @return Redirects to the PDF if found
     */
    @GetMapping(value = "/pdf/{type}/{year}/{number}")
    @Operation(summary = "Get an original PDF version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Found and redirecting to the PDF URL"),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input")
    })
    ResponseEntity<Void> getPdf(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam(required = false) String version);


    @GetMapping(value = "/pdf/{type}/{monarch}/{years}/{number}")
    @Operation(summary = "Get the original PDF version by regnal year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Found and redirecting to the PDF URL"),
            @ApiResponse(responseCode = "404", description = "Document not found"),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input")
    })
    ResponseEntity<Void> getPdfWithRegnalYear(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam(required = false) String version);


    @GetMapping(value = "/thumbnail/{type}/{year}/{number}")
    @Operation(summary = "Get the thumbnail of a PDF document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Found and redirecting to the thumbnail URL"),
            @ApiResponse(responseCode = "404", description = "Thumbnail not found"),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input")
    })
    ResponseEntity<Void> getPdfThumbnail(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam(required = false) String version);


    @GetMapping(value = "/thumbnail/{type}/{monarch}/{years}/{number}")
    @Operation(summary = "Get the thumbnail of a PDF document by regnal year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Found and redirecting to the thumbnail URL"),
            @ApiResponse(responseCode = "404", description = "Thumbnail not found"),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input")
    })
    ResponseEntity<Void> getPdfThumbnailWithRegnalYear(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam(required = false) String version);
}


