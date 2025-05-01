package uk.gov.legislation.endpoints.ld.interpretation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.InterpretationQuery;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(
    path = "/ld/interpretation",
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
public class InterpretationController {

    private final InterpretationQuery query;

    private final ContentNegotiationManager negotiation;

    public InterpretationController(InterpretationQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @GetMapping("/{type}/{year}/{number}")
    public ResponseEntity<?> getCalendar(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam(required = false) String version) throws Exception {
        return getEither(request, type, Integer.toString(year), number, version);
    }

    @GetMapping("/{type}/{reign}/{session}/{number}")
    public ResponseEntity<?> getRegnal(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable String reign,
            @PathVariable String session,
            @PathVariable int number,
            @RequestParam(required = false) String version) throws Exception {
        String regnal = reign + "/" + session;
        return getEither(request, type, regnal, number, version);
    }

    private ResponseEntity<?> getEither(NativeWebRequest request,
            String type,
            String year,
            int number,
            String version) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, year, number, version, media.toString());
            return ResponseEntity.ok(data);
        }
        return query.get(type, year, number, version)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
