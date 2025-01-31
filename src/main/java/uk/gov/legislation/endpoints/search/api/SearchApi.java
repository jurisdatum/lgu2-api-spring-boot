package uk.gov.legislation.endpoints.search.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.responses.PageOfDocuments;

import java.io.IOException;

@Tag(name = "Search")
public interface SearchApi {

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> searchByAtom(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language


    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PageOfDocuments> searchByJson(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language


    ) throws IOException, InterruptedException;

}
