package uk.gov.legislation.endpoints.ld.reign;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.converters.ld.ReignConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ReignQuery;

@RestController
public class ReignController implements ReignApi {

    private final ContentNegotiationManager negotiation;
    private final ReignQuery query;

    public ReignController(ContentNegotiationManager negotiation, ReignQuery query) {
        this.negotiation = negotiation;
        this.query = query;
    }

    @Override
    public ResponseEntity<?> get(NativeWebRequest request, String id) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String result = query.get(id, media.toString());
            return ResponseEntity.ok()
                .contentType(media)
                .body(result);
        }
        return query.get(id)
            .map(ReignConverter::convert)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
