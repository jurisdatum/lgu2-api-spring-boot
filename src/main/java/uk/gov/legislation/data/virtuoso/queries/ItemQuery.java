package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import uk.gov.legislation.api.responses.ld.Item;
import uk.gov.legislation.converters.ld.ItemConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;

import java.io.IOException;
import java.util.Optional;

@Repository
public class ItemQuery {

    private final Virtuoso virtuoso;

    public ItemQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public String makeSparqlQuery(String type, int year, int number) {
        String uri = "http://www.legislation.gov.uk/id/%s/%s/%d".formatted(type, year, number);
        return "CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o . }".formatted(uri, uri);
    }

    public String get(String type, int year, int number, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, number);
        return virtuoso.query(query, format);
    }

    public Optional<Item> get(String type, int year, int number) throws IOException, InterruptedException {
        String json = get(type, year, number, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        return Optional.ofNullable(graph)
            .map(grph -> (ObjectNode) grph.get(0))
            .map(ItemLD::convert)
            .map(ItemConverter::convert);
    }

}
