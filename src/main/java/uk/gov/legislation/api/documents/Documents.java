package uk.gov.legislation.api.documents;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.marklogic.Search;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.ShortTypes;

import java.io.IOException;

@RestController
public class Documents {

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response docs(@PathVariable String type, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!ShortTypes.isValidShortType(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        SearchResults results = Search.byType(type, page);
        return SearchResultsConverter.convert(results);
    }

    @GetMapping(value = "/documents/{type}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public String feed(@PathVariable String type, @RequestParam(value = "page", defaultValue = "1") int page) {
        if (!ShortTypes.isValidShortType(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            return Search.byTypeAtom(type, page);
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* and year */

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response typeAndYear(@PathVariable String type, @PathVariable int year, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!ShortTypes.isValidShortType(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        SearchResults results = Search.byTypeAndYear(type, year, page);
        return SearchResultsConverter.convert(results);
    }

    @GetMapping(value = "/documents/{type}/{year:[\\d]+}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public String typeAndYearFeed(@PathVariable String type, @PathVariable int year, @RequestParam(value = "page", defaultValue = "1") int page) throws IOException, InterruptedException {
        if (!ShortTypes.isValidShortType(type))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        String results = Search.byTypeAndYearAtom(type, year, page);
        return results;
    }

}
