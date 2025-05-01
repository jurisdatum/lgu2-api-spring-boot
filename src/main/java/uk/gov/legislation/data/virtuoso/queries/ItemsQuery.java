package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Item;
import uk.gov.legislation.api.responses.ld.PageOfItems;
import uk.gov.legislation.converters.ld.ItemConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class ItemsQuery {

    private final Virtuoso virtuoso;

    public ItemsQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public static String makeSparqlQuery(String type, int year, int pageSize, int offset) {
        return """
            PREFIX : <http://www.legislation.gov.uk/def/legislation/>
            CONSTRUCT { ?s ?p ?o . }
            WHERE {
              {
                SELECT DISTINCT ?s
                WHERE {
                  ?s a [:acronym '%s'] ;
                    :year %d ;
                    :number ?number .
                }
                ORDER BY DESC(?number)
                LIMIT %d
                OFFSET %d
              }
              ?s ?p ?o .
            }
            """.formatted(type, year, pageSize, offset);
    }

    public String get(String type, int year, int pageSize, int offset, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, pageSize, offset);
        return virtuoso.query(query, format);
    }

    public Optional<PageOfItems> get(String type, int year, int pageSize, int offset) throws IOException, InterruptedException {
        String json = get(type, year, pageSize, offset, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        if (graph == null)
            return Optional.empty();
        List<Item> items = StreamSupport.stream(graph.spliterator(), false)
            .map(ObjectNode.class::cast)
            .map(ItemLD::convert)
            .map(ItemConverter::convert)
            .sorted(Comparator.comparing(o -> ((Item) o).number)
            .reversed())
            .toList();
        PageOfItems page = new PageOfItems();
        page.meta = new PageOfItems.Meta();
        page.meta.type = type;
        page.meta.year = year;
        page.meta.page = offset / pageSize + 1;
        page.meta.pageSize = pageSize;
        page.items = items;
        return Optional.of(page);
    }

}
