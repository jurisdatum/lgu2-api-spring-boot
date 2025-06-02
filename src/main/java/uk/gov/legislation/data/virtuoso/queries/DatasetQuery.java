package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.DatasetLD;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class DatasetQuery {

    private final Virtuoso virtuoso;

    public DatasetQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    public static String makeSparqlQuery(String id) {
        String uri = String.format("http://www.legislation.gov.uk/id/dataset/%s", id);
        return makeSingleConstructQuery(uri);
    }

    public String get(String id, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(id);
        return virtuoso.query(query, format);
    }

    public Optional<DatasetLD> get(String reignId) throws IOException, InterruptedException {
        String json = get(reignId, "application/ld+json");
        return Graph.extractFirstObject(json, DatasetLD.class);
    }

}
