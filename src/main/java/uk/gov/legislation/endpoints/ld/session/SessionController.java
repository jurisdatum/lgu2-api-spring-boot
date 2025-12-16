package uk.gov.legislation.endpoints.ld.session;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.SessionQuery;

@RestController
public class SessionController implements SessionApi {

    private final ContentNegotiationManager negotiation;
    private final SessionQuery query;

    public SessionController(ContentNegotiationManager negotiation, SessionQuery query) {
        this.negotiation = negotiation;
        this.query = query;
    }

    @Override
    public ResponseEntity <?> getSessionByLegislatureReign(NativeWebRequest request, String legislature,
        String reign, String session) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();

        if (Virtuoso.Formats.contains(media.toString())) {
            String result = query.fetchRawData(legislature, reign, session, media.toString());
            return ResponseEntity.ok()
                .contentType(media)
                .body(result);
        }

        return query.fetchMappedData(legislature, reign, session)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @Override
    public ResponseEntity <?> getEnglishParliamentSession(NativeWebRequest request,String session) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();

        if (Virtuoso.Formats.contains(media.toString())) {
            String result = query.fetchRawData(session, media.toString());
            return ResponseEntity.ok()
                .contentType(media)
                .body(result);
        }

        return query.fetchMappedData(session)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
