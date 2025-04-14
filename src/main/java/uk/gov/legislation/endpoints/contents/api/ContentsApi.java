package uk.gov.legislation.endpoints.contents.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.parameters.*;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.responses.TableOfContents;

import java.util.Locale;
import java.util.Optional;

@Tag(name = "Tables of contents")
public interface ContentsApi {

    /* CLML */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/xml")
    @Operation(
        summary = "get a document's table of conents (calendar year)",
        parameters = {
            @Parameter(
                name = "Accept-Language",
                description = "language of the document",
                in = ParameterIn.HEADER,
                schema = @Schema(type = "string", allowableValues = { "en", "cy" }, examples = "en")
            )
        }
    )
    ResponseEntity<String> getDocumentContentsClml(
            @PathVariable @Type String type,
            @PathVariable @Year int year,
            @PathVariable @Number int number,
            @RequestParam @Version Optional<String> version,
            Locale locale);

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/xml")
    @Operation(
        summary = "get a document's table of conents (regnal year)",
        parameters = {
            @Parameter(
                name = "Accept-Language",
                description = "language of the document",
                in = ParameterIn.HEADER,
                schema = @Schema(type = "string", allowableValues = { "en", "cy" }, examples = "en")
            )
        }
    )
    ResponseEntity<String> getDocumentContentsClml(
            @PathVariable @Type String type,
            @PathVariable @Monarch String monarch,
            @PathVariable @Years String years,
            @PathVariable @Number int number,
            @RequestParam @Version Optional<String> version,
            Locale locale);

    /* Akoma Ntoso */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentContentsAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            Locale locale);

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentContentsAkn(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            Locale locale);

    /* JSON */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            Locale locale);

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @RequestParam Optional<String> version,
            Locale locale);

}
