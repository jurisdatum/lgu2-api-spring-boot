package uk.gov.legislation.endpoints.ld.classinfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.ClassQuery;

@RestController
public class ClassInfoController implements ClassInfoApi {

    private final ClassQuery query;

    private final ContentNegotiationManager negotiation;

    public ClassInfoController(ClassQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @Override
    public ResponseEntity<?> getClassInfo(NativeWebRequest request, String name) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(name, media.toString());
            return ResponseEntity.ok(data);
        }
        return query.get(name)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
