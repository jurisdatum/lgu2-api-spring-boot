package uk.gov.legislation.endpoints.document.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.params.Number;
import uk.gov.legislation.params.*;

import java.util.Optional;

/**
 * API for document retrieval and transformation operations.
 */
@Tag(name = "Documents")
public interface DocumentApi {

    /**
     * Retrieves document content in CLML format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/xml")
    @Operation(summary = "get a document with a calendar year")
    ResponseEntity<String> getDocumentClml(
            @PathVariable @Type String type,
            @PathVariable @Year int year,
            @PathVariable @Number int number,
            @RequestParam @Version Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    @GetMapping(value = "/document/{type}/{monarch}/{years}/{number}", produces = "application/xml")
    @Operation(summary = "get a document with a regnal year")
    ResponseEntity<String> getDocumentClml(
            @PathVariable @Type String type,
            @PathVariable @Monarch String monarch,
            @PathVariable @Years String years,
            @PathVariable @Number int number,
            @RequestParam @Version Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    /**
     * Retrieves document content in AKN format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    @GetMapping(value = "/document/{type}/{monarch}/{years}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentAkn(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    /**
     * Retrieves document content in HTML format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "text/html")
    ResponseEntity<String> getDocumentHtml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    @GetMapping(value = "/document/{type}/{monarch}/{years}/{number}", produces = "text/html")
    ResponseEntity<String> getDocumentHtml(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    /**
     * Retrieves document metadata and HTML content in JSON format.
     */
    @GetMapping(value = "/document/{type}/{year}/{number}", produces = "application/json")
    ResponseEntity<Document> getDocumentJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

    @GetMapping(value = "/document/{type}/{monarch}/{years}/{number}", produces = "application/json")
    ResponseEntity<Document> getDocumentJson(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language);

}
