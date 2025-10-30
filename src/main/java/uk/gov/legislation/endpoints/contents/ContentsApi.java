package uk.gov.legislation.endpoints.contents;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Operation(summary = "Retrieve a Document’s table of contents by calendar year")
    @AcceptLanguage
    ResponseEntity<String> getDocumentContentsClml(
        @PathVariable @Type String type,
        @PathVariable @Year int year,
        @PathVariable @Number int number,
        @RequestParam @Version Optional<String> version,
        Locale locale) throws Exception;


    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/xml")
    @Operation(summary = "Retrieve a Document’s table of contents by regnal year")
    @AcceptLanguage
    ResponseEntity<String> getDocumentContentsClml(
        @PathVariable @Type String type,
        @PathVariable @Monarch String monarch,
        @PathVariable @Years String years,
        @PathVariable @Number int number,
        @RequestParam @Version Optional<String> version,
        Locale locale) throws Exception;


    /* Akoma Ntoso */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentContentsAkn(
        String type,
        int year,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/akn+xml")
    ResponseEntity<String> getDocumentContentsAkn(
        String type,
        String monarch,
        String years,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

    /* JSON */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
        String type,
        int year,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/json")
    ResponseEntity<TableOfContents> getDocumentContentsJson(
        String type,
        String monarch,
        String years,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

    /* Word (.docx) */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<byte[]> docx(
        String type,
        int year,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ResponseEntity<byte[]> docx(
        String type,
        String monarch,
        String years,
        int number,
        Optional<String> version,
        Locale locale) throws Exception;

}
