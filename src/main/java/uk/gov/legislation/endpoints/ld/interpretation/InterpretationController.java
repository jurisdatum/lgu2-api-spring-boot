package uk.gov.legislation.endpoints.ld.interpretation;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.InterpretationQuery;

import java.util.Optional;

@RestController
public class InterpretationController {

    private final InterpretationQuery query;

    public InterpretationController(InterpretationQuery query) {
        this.query = query;
    }

    @GetMapping(
        value = "/ld/interpretation/{type}/{year}/{number}",
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
    public ResponseEntity<Object> get(NativeWebRequest request, @PathVariable String type, @PathVariable int year, @PathVariable int number, @RequestParam(required = false) String version) throws Exception {
        MediaType media = new HeaderContentNegotiationStrategy().resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, year, number, version, media.toString());
            return ResponseEntity.ok(data);
        }
        Optional<Interpretation> response = query.get(type, year, number, version);
        if (response.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(response.get());
    }

}
