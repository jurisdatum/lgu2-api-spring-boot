package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.Virtuoso;

import java.io.IOException;
@Component
public class ClassInfo {

    private final Virtuoso virtuoso;

    public ClassInfo(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    /**
     * Query for getting Class Information
     */
    public String getClassData(String name, String format) throws IOException, InterruptedException {
        String query = """
                SELECT ?s ?p ?o
                WHERE {
                   BIND(<%s%s> AS ?s)
                   ?s ?p ?o
                }
            """.formatted(Resources.Leg.Prefix, name);
        return virtuoso.query(query, format);
    }

}
