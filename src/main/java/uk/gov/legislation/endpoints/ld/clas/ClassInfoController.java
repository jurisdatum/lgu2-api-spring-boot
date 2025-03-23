package uk.gov.legislation.endpoints.ld.clas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.queries.SparqlQueries;
import uk.gov.legislation.endpoints.ld.Helper;

@RestController
public class ClassInfoController implements ClassInfoApi {

    private final SparqlQueries queries;

    public ClassInfoController(SparqlQueries queries) {
        this.queries = queries;
    }

    @Override
    public ResponseEntity<String> getClassInfo(String name, String accept) throws Exception {
        String format = Helper.getFormat(accept);
        String results = queries.getClassData(name, format);
        return ResponseEntity.ok()
            .header("Content-Type", format)
            .body(results);
    }

}
