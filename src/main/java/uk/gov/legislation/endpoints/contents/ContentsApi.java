package uk.gov.legislation.endpoints.contents;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.legislation.api.Tags;
import uk.gov.legislation.api.headers.AcceptLanguage;
import uk.gov.legislation.api.parameters.*;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.responses.TableOfContents;

import java.util.Locale;
import java.util.Optional;

@Tags.TablesOfContents
public interface ContentsApi {

    /* CLML */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/xml")
    @Operation(summary = "Retrieve a Document's table of contents by calendar year")
    @AcceptLanguage
    ResponseEntity<StreamingResponseBody> getDocumentContentsClml(
        @PathVariable @Type String type,
        @PathVariable @Year int year,
        @PathVariable @Number int number,
        @RequestParam @Version Optional<String> version,
        Locale locale);


    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/xml")
    @Operation(summary = "Retrieve a Document's table of contents by regnal year")
    @AcceptLanguage
    ResponseEntity<StreamingResponseBody> getDocumentContentsClml(
        @PathVariable @Type String type,
        @PathVariable @Monarch String monarch,
        @PathVariable @Years String years,
        @PathVariable @Number int number,
        @RequestParam @Version Optional<String> version,
        Locale locale);


    /* Akoma Ntoso */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    ResponseEntity<StreamingResponseBody> getDocumentContentsAkn(
        @PathVariable String type,
        @PathVariable int year,
        @PathVariable int number,
        @RequestParam Optional<String> version,
        Locale locale);

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/akn+xml")
    ResponseEntity<StreamingResponseBody> getDocumentContentsAkn(
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
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
        @PathVariable String type,
        @PathVariable String monarch,
        @PathVariable String years,
        @PathVariable int number,
        @RequestParam Optional<String> version,
        Locale locale) throws Exception;

    /* Word (.docx) */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<StreamingResponseBody> docx(
        @PathVariable String type,
        @PathVariable int year,
        @PathVariable int number,
        @RequestParam Optional<String> version,
        Locale locale);

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<StreamingResponseBody> docx(
        @PathVariable String type,
        @PathVariable String monarch,
        @PathVariable String years,
        @PathVariable int number,
        @RequestParam Optional<String> version,
        Locale locale);

}
