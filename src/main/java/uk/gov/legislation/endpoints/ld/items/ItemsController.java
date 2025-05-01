package uk.gov.legislation.endpoints.ld.items;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ItemsQuery;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(
    path = "/ld/items",
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
public class ItemsController {

    private final ItemsQuery query;

    private final ContentNegotiationManager negotiation;

    public ItemsController(ItemsQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @GetMapping("/{type}/{year}")
    public ResponseEntity<?> typeAndYear(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable int year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {
        final int offset = (page - 1) * pageSize;
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, year, pageSize, offset, media.toString());
            return ResponseEntity.ok(data);
        }
        return query.get(type, year, pageSize, offset)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
