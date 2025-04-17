package uk.gov.legislation.endpoints.ld.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.responses.ld.Item;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ItemQuery;

import java.util.Optional;

@RestController
public class ItemController {

    private final ItemQuery query;

    public ItemController(ItemQuery query) {
        this.query = query;
    }

    @GetMapping(
        value = "/ld/item/{type}/{year}/{number}",
        produces = {
            "application/xml",
            "application/json",
            "application/rdf+xml",
            "application/rdf+json",
            "application/ld+json",
            "application/sparql-results+json",
            "application/sparql-results+xml",
            "text/csv",
            "text/plain",
            "text/turtle"
        }
    )
    public ResponseEntity<Object> get(NativeWebRequest request, @PathVariable String type, @PathVariable int year, @PathVariable int number) throws Exception {
        MediaType media = new HeaderContentNegotiationStrategy().resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, year, number, media.toString());
            return ResponseEntity.ok(data);
        }
        Optional<Item> response = query.get(type, year, number);
        if (response.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(response.get());
    }

}
