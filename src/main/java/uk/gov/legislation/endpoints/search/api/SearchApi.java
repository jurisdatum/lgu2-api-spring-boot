package uk.gov.legislation.endpoints.search.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.endpoints.documents.DocumentList;

import java.io.IOException;

@Tag(name = "Search")
@Validated
public interface SearchApi {

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> searchAtom(
            @RequestParam @NotBlank String title,
            @RequestParam(required = false, defaultValue = "1") int page

    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<DocumentList> searchJson(
            @RequestParam @NotBlank String title,
            @RequestParam(required = false, defaultValue = "1") int page

    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search-by-filter", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> searchByAtom(
            @RequestParam @NotBlank String title,
            @RequestParam @NotBlank String type,
            @RequestParam @NotBlank String year,
            @RequestParam @NotBlank String number,
            @RequestParam(required = false, defaultValue = "1") int page

    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search-by-filter", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<DocumentList> searchByJson(
            @RequestParam @NotBlank String title,
            @RequestParam @NotBlank String type,
            @RequestParam @NotBlank String year,
            @RequestParam @NotBlank String number,
            @RequestParam(required = false, defaultValue = "1") int page

    ) throws IOException, InterruptedException;



}

