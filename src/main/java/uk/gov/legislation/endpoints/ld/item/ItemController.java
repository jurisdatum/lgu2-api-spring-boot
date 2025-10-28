package uk.gov.legislation.endpoints.ld.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ItemQuery;

@RestController
public class ItemController implements ItemApi {

    private final ItemQuery query;

    private final ContentNegotiationManager negotiation;

    public ItemController(ItemQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }
    @Override
    public ResponseEntity<?> get(NativeWebRequest request, @PathVariable String type, @PathVariable int year, @PathVariable int number) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, year, number, media.toString());
            return ResponseEntity.ok(data);
        }
        return query.get(type, year, number)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
