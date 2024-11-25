package uk.gov.legislation.endpoints.document.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.endpoints.document.api.params.*;
import uk.gov.legislation.endpoints.document.api.params.Number;

import java.util.Optional;

/**
 * API interface for accessing document fragments in various formats.
 */
@Tag(name = "Document fragments")

public interface FragmentApi {

    /* CLML */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/xml")
    @Operation(summary = "get a document fragment, e.g., a section (calendar year)")
    ResponseEntity <String> getFragmentClml(
            @PathVariable @Type String type,
            @PathVariable @Year int year,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam @Version Optional <String> version);

    @GetMapping(value = "/fragment/{type}/{monarch}/{years}/{number}/{section}", produces = "application/xml")
    @Operation(summary = "get a document fragment, e.g., a section (regnal year)")
    ResponseEntity <String> getFragmentClml(
            @PathVariable @Type String type,
            @PathVariable @Monarch String monarch,
            @PathVariable @Years String years,
            @PathVariable @Number int number,
            @PathVariable @Section String section,
            @RequestParam @Version Optional <String> version);

    /* Akoma Ntoso */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/akn+xml")
    ResponseEntity<String> getFragmentAkn(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

    @GetMapping(value = "/fragment/{type}/{monarch}/{years}/{number}/{section}", produces = "application/akn+xml")
    ResponseEntity<String> getFragmentAkn(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

    /* HTML5 */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "text/html")
    ResponseEntity<String> getFragmentHtml(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

    @GetMapping(value = "/fragment/{type}/{monarch}/{years}/{number}/{section}", produces = "text/html")
    ResponseEntity<String> getFragmentHtml(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

    /* JSON */

    @GetMapping(value = "/fragment/{type}/{year}/{number}/{section}", produces = "application/json")
    ResponseEntity<DocumentApi.Response> getFragmentJson(
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

    @GetMapping(value = "/fragment/{type}/{monarch}/{years}/{number}/{section}", produces = "application/json")
    ResponseEntity<DocumentApi.Response> getFragmentJson(
            @PathVariable String type,
            @PathVariable String monarch,
            @PathVariable String years,
            @PathVariable int number,
            @PathVariable String section,
            @RequestParam Optional<String> version);

}
