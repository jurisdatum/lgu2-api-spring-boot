package uk.gov.legislation.endpoints.ld.reign.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ReignQuery;
import uk.gov.legislation.endpoints.ld.reign.api.ReignApi;

@RestController
public class ReignController implements ReignApi {

    private final ContentNegotiationManager negotiation;
    private final ReignQuery query;

    public ReignController(ContentNegotiationManager negotiation, ReignQuery query) {
        this.negotiation = negotiation;
        this.query = query;
    }

    @Override
    public ResponseEntity <?> getReignInfo(NativeWebRequest request,String reign) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String result = query.getReignData(reign, media.toString());
            return ResponseEntity.ok()
                .contentType(media)
                .body(result);
        }
        return query.getReignAsJsonLd(reign)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }
}
