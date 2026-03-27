package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import uk.gov.legislation.api.responses.ld.Legislature;
import uk.gov.legislation.converters.ld.LegislatureConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.LegislatureLD;

import java.io.IOException;
import java.util.Optional;

@Repository
public class LegislatureQuery {

    private final Virtuoso virtuoso;

    public LegislatureQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public String makeSparqlQuery(String name) {
        String uri = "http://www.legislation.gov.uk/id/legislature/%s".formatted(name);
        return Query.makeSingleConstructQuery(uri);
    }

    public String get(String name, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(name);
        return virtuoso.query(query, format);
    }

    public Optional<Legislature> get(String name) throws IOException, InterruptedException {
        String json = get(name, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        if (graph == null)
            return Optional.empty();
        if (graph.isEmpty())
            return Optional.empty();
        return Optional.of(graph.get(0))
            .map(ObjectNode.class::cast)
            .map(LegislatureLD::convert)
            .map(LegislatureConverter::convert);
    }

}
