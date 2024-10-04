package uk.gov.legislation.api.documents;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.marklogic.Search;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.Types;

import java.io.IOException;

@RestController
@Tag(name = "Document lists", description = "lists of documents")
public class Documents {

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentList docs(@PathVariable String type, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type))
            throw new UnknownTypeException(type);
        SearchResults results = Search.byType(type, page);
        return Converter.convert(results);
    }

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public String feed(@PathVariable String type, @RequestParam(value = "page", defaultValue = "1") int page) {
        if (!Types.isValidShortType(type))
            throw new UnknownTypeException(type);
        try {
            return Search.byTypeAtom(type, page);
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* and year */

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentList typeAndYear(@PathVariable String type, @PathVariable int year, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type))
            throw new UnknownTypeException(type);
        SearchResults results = Search.byTypeAndYear(type, year, page);
        return Converter.convert(results);
    }

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public String typeAndYearFeed(@PathVariable String type, @PathVariable int year, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!Types.isValidShortType(type))
            throw new UnknownTypeException(type);
        String results = Search.byTypeAndYearAtom(type, year, page);
        return results;
    }

    public static class UnknownTypeException extends ResponseStatusException {

        private UnknownTypeException(String type) {
            super(HttpStatus.BAD_REQUEST, "unknown doc type: " + type);
        }

    }

}
