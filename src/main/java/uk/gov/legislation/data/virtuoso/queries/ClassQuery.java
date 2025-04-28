package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Clazz;
import uk.gov.legislation.converters.ld.ClassConverter;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.ClassLD;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class ClassQuery {

    private final Virtuoso virtuoso;

    public ClassQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    String makeSparqlQuery(String name) {
        String uri = Resources.Leg.Prefix + name;
        return makeSingleConstructQuery(uri);
    }

    public String get(String name, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(name);
        return virtuoso.query(query, format);
    }

    public Optional<Clazz> get(String name) throws IOException, InterruptedException {
        String json = get(name, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        return Optional.ofNullable(graph)
            .map(grph -> (ObjectNode) grph.get(0))
            .map(ClassLD::convert)
            .map(ClassConverter::convert);
    }

}
