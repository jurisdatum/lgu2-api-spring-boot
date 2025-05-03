package uk.gov.legislation.endpoints.ld.legislature;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.LegislatureQuery;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(
    path = "/ld/legislature",
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
public class LegislatureController {

    private final LegislatureQuery query;

    private final ContentNegotiationManager negotiation;

    public LegislatureController(LegislatureQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getCalendar(NativeWebRequest request, @PathVariable String name) throws Exception {
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
