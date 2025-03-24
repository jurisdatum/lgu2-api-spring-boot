package uk.gov.legislation.endpoints.ld.classinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.queries.ClassInfo;
import uk.gov.legislation.endpoints.ld.Helper;

@RestController
public class ClassInfoController implements ClassInfoApi {

    private final ClassInfo queries;

    public ClassInfoController(ClassInfo queries) {
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
