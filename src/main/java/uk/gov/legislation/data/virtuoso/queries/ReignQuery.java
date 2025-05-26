package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.ReignLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class ReignQuery {

    private final Virtuoso virtuoso;

    public ReignQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    public static String makeSparqlQuery(String reign) {
        String uri = String.format("http://www.legislation.gov.uk/id/reign/%s", reign);
        return makeSingleConstructQuery(uri);
    }

    public String get(String reign, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(reign);
        return virtuoso.query(query, format);
    }

    public Optional<ReignLD> get(String reignId) throws IOException, InterruptedException {
        String json = get(reignId, "application/ld+json");
        ArrayNode graph = Graph.extract(json);

        if (graph == null || graph.isEmpty())
            return Optional.empty();

        return Optional.of(graph.get(0))
            .map(ObjectNode.class::cast)
            .map(ReignLD::convert);
    }

}


