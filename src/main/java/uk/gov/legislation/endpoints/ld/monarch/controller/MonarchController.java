package uk.gov.legislation.endpoints.ld.monarch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.MonarchQuery;
import uk.gov.legislation.endpoints.ld.monarch.api.MonarchApi;

@RestController
public class MonarchController implements MonarchApi {

    private final ContentNegotiationManager negotiationManager;
    private final MonarchQuery query;

    public MonarchController(ContentNegotiationManager negotiationManager, MonarchQuery query) {
        this.negotiationManager = negotiationManager;
        this.query = query;
    }

    @Override
    public ResponseEntity<?> getMonarchInfo(NativeWebRequest request, String monarch) throws Exception {
        MediaType mediaType = negotiationManager.resolveMediaTypes(request).getFirst();

        if (Virtuoso.Formats.contains(mediaType.toString())) {
            String result = query.getMonarchData(monarch, mediaType.toString());
            return ResponseEntity.ok()
                .contentType(mediaType)
                .body(result);
        }

        return query.getMonarchAsJsonLd(monarch)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}











