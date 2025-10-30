package uk.gov.legislation.endpoints.contents;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.parameters.*;
import uk.gov.legislation.api.responses.TableOfContents;

import java.util.Locale;
import java.util.Optional;

@ApiTags.TablesOfContents
public interface ContentsApi {

    /* CLML */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/xml")
    @ApiOperations.ContentsOfCalendarYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<String> getDocumentContentsClml(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Year int year,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;


    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/xml")
    @ApiOperations.ContentsOfRegnalYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<String> getDocumentContentsClml(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Monarch String monarch,
        @PathVariable @PathVars.Years String years,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;


    /* Akoma Ntoso */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/akn+xml")
    @ApiOperations.ContentsOfCalendarYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<String> getDocumentContentsAkn(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Year int year,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/akn+xml")
    @ApiOperations.ContentsOfRegnalYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<String> getDocumentContentsAkn(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Monarch String monarch,
        @PathVariable @PathVars.Years String years,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

    /* JSON */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/json")
    @ApiOperations.ContentsOfCalendarYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<TableOfContents> getDocumentContentsJson(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Year int year,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/json")
    @ApiOperations.ContentsOfRegnalYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<TableOfContents> getDocumentContentsJson(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Monarch String monarch,
        @PathVariable @PathVars.Years String years,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

    /* Word (.docx) */

    @GetMapping(value = "/contents/{type}/{year}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    @ApiOperations.ContentsOfCalendarYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<byte[]> docx(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Year int year,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

    @GetMapping(value = "/contents/{type}/{monarch}/{years}/{number}", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    @ApiOperations.ContentsOfRegnalYear
    @ApiHeaders.AcceptLanguage
    ResponseEntity<byte[]> docx(
        @PathVariable @PathVars.Type String type,
        @PathVariable @PathVars.Monarch String monarch,
        @PathVariable @PathVars.Years String years,
        @PathVariable @PathVars.Number int number,
        @RequestParam @ApiParams.Version Optional<String> version,
        Locale locale) throws Exception;

}
