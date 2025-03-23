package uk.gov.legislation.endpoints.classinfo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.queries.SparqlQueries;
import uk.gov.legislation.endpoints.classinfo.api.ClassInfoApi;
import uk.gov.legislation.endpoints.ld.Helper;

@RestController
public class ClassInfoApiController implements ClassInfoApi {

    private final SparqlQueries queries;

    public ClassInfoApiController(SparqlQueries queries) {
        this.queries = queries;
    }

    @Override
    public ResponseEntity<String> getClassInfo(String className, String accept) throws Exception {
        String format = Helper.getFormat(accept);
        String results = queries.getClassData(className, format);
        return ResponseEntity.ok()
            .header("Content-Type", format)
            .body(results);
    }

}
