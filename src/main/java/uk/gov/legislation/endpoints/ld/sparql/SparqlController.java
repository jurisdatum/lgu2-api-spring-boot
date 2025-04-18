package uk.gov.legislation.endpoints.ld.sparql;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.endpoints.ld.Helper;

@RestController
public class SparqlController implements SparqlApi {

    private final Virtuoso virtuoso;

    public SparqlController(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    @Override
    public ResponseEntity<String> sparql(String query, String accept) throws Exception {
        String format = Helper.getFormat(accept);
        String response = virtuoso.query(query, format);
        return ResponseEntity.ok()
            .header("Content-Type", format)
            .body(response);
    }

}
