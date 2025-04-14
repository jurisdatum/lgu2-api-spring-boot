package uk.gov.legislation.endpoints.ld.item;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.data.virtuoso.model2.Item;

@RestController
public class ItemController {

    private final uk.gov.legislation.data.virtuoso.queries.Item2 query;

    public ItemController(uk.gov.legislation.data.virtuoso.queries.Item2 query) {
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
        if (media == null || MediaType.ALL.equals(media) || MediaType.APPLICATION_JSON.equals(media) || MediaType.APPLICATION_XML.equals(media)) {
            Item response = query.get(type, year, number);
            return ResponseEntity.ok(response);
        }
        String data = query.get(type, year, number, media.toString());
        return ResponseEntity.ok(data);
    }

}
