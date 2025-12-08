package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.PageOfItems;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.Item;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class ItemsQuery {

    private final Virtuoso virtuoso;

    public ItemsQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public static String makeSparqlQuery(String type, ItemSort sort, int pageSize, int offset) {
        if (sort == null)
            sort = ItemSort.YEAR_DESC;
        return """
            PREFIX : <http://www.legislation.gov.uk/def/legislation/>
            CONSTRUCT { ?s ?p ?o . }
            WHERE {
              {
                SELECT DISTINCT ?s
                WHERE {
                  ?s a [:acronym '%s'] ;
                    :year ?year ;
                    :number ?number .
                  OPTIONAL { ?s :citation ?citation }
                  OPTIONAL { ?s :title ?title }
                }
                ORDER BY %s
                LIMIT %d
                OFFSET %d
              }
              ?s ?p ?o .
            }
            """.formatted(type, sort.sparql(), pageSize, offset);
    }

    public static String makeSparqlQuery(String type, int year, ItemSort sort, int pageSize, int offset) {
        if (sort == null)
            sort = ItemSort.NUMBER_ASC;
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
                  OPTIONAL { ?s :citation ?citation }
                  OPTIONAL { ?s :title ?title }
                }
                ORDER BY %s
                LIMIT %d
                OFFSET %d
              }
              ?s ?p ?o .
            }
            """.formatted(type, year, sort.sparql(), pageSize, offset);
    }

    public String get(String type, Integer year, ItemSort sort, int pageSize, int offset, String format) throws IOException, InterruptedException {
        String query = (year == null)
            ? makeSparqlQuery(type, sort, pageSize, offset)
            : makeSparqlQuery(type, year, sort, pageSize, offset);
        return virtuoso.query(query, format);
    }

    public Optional<PageOfItems> get(String type, Integer year, ItemSort sort, int pageSize, int offset) throws IOException, InterruptedException {
        String json = get(type, year, sort, pageSize, offset, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        if (graph == null)
            return Optional.empty();
        List<Item> items = StreamSupport.stream(graph.spliterator(), false)
            .map(ObjectNode.class::cast)
            .map(Item::convert)
            .sorted(ItemSort.comparator(sort))
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
