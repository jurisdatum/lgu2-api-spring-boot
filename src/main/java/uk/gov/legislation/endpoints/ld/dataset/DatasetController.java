package uk.gov.legislation.endpoints.ld.dataset;

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
import uk.gov.legislation.converters.ld.DataSetConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.DatasetQuery;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(
    path = "/ld/dataset",
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
public class DatasetController {

    private final DatasetQuery query;

    private final ContentNegotiationManager negotiation;

    public DatasetController(DatasetQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @GetMapping("/{*id}")
    public ResponseEntity<?> get(NativeWebRequest request, @PathVariable String id) throws Exception {
        if (id.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        id = id.substring(1); // trim leading slash
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(id, media.toString());
            return ResponseEntity.ok(data);
        }
        return query.get(id)
            .map(DataSetConverter::convert)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
