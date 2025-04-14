package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.converters.ItemConverter;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;

import java.io.IOException;

@Repository("virtuosoQueryItem")
public class Item2 {

    private final Virtuoso virtuoso;

    public Item2(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public String makeSparqlQuery(String type, int year, int number) {
        String uri = "http://www.legislation.gov.uk/id/%s/%s/%d".formatted(type, year, number);
        return "CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o . }".formatted(uri, uri);
    }

    public String get(String type, int year, int number, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, number);
        return virtuoso.query(query, format);
    }

    public uk.gov.legislation.data.virtuoso.model2.Item get(String type, int year, int number) throws IOException, InterruptedException {
        String json = get(type, year, number, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        ObjectNode item0 = (ObjectNode) graph.get(0);
        ItemLD item1 = ItemLD.convert(item0);
        return ItemConverter.convert(item1);
    }

}
