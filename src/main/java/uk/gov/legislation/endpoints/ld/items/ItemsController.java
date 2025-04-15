package uk.gov.legislation.endpoints.ld.items;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.responses.ld.PageOfItems;

@RestController
public class ItemsController {


    private final uk.gov.legislation.data.virtuoso.queries.Items2 query;

    public ItemsController(uk.gov.legislation.data.virtuoso.queries.Items2 query) {
        this.query = query;
    }

    @GetMapping(
        value = "/ld/items/{type}/{year}",
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
    public ResponseEntity<Object> typeAndYear(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable int year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) throws Exception {

        final int offset = (page - 1) * pageSize;
        MediaType media = new HeaderContentNegotiationStrategy().resolveMediaTypes(request).getFirst();
        if (media == null || MediaType.ALL.equals(media) || MediaType.APPLICATION_JSON.equals(media) || MediaType.APPLICATION_XML.equals(media)) {
            PageOfItems response = query.get(type, year, pageSize, offset);
            return ResponseEntity.ok(response);
        }
        String data = query.get(type, year, pageSize, offset, media.toString());
        return ResponseEntity.ok(data);
    }

}
