package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.JsonResults;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.model.Item;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated(forRemoval = true)
@Repository
public class Items {

    private final Virtuoso virtuoso;

    public Items(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public String makeSparqlQuery(String type, int year, int limit, int offset) throws IOException, InterruptedException {
        return """
                 PREFIX leg: <http://www.legislation.gov.uk/def/legislation/>
                 SELECT * {
                 ?item a [leg:acronym '%s'] ;
                 leg:year %d ;
                 leg:year ?year ;
                 leg:title ?title ;
                 leg:number ?number ;
                 leg:citation ?cite .
                 }
                 ORDER BY DESC(?number)
                 LIMIT %d
                 OFFSET %d
                """.formatted(type, year,limit, offset);
    }

    public String get(String type, int year, int limit, int offset, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, limit, offset);
        return virtuoso.query(query, format);
    }

    public List<Item> get(String type, int year, int limit, int offset) throws IOException, InterruptedException {
        String json = get(type, year, limit, offset, "application/sparql-results+json");
        JsonResults parsed = JsonResults.parse(json);
        return parsed.results.bindings.stream().map(Items::map).toList();
    }

    private static Item map(Map<String, JsonResults.Value> bindings) {
        Item item = new Item();
        item.uri = URI.create(bindings.get("item").value());
        item.types = Collections.emptyList();
        item.year = Integer.parseInt(bindings.get("year").value());
        item.number = Integer.parseInt(bindings.get("number").value());
        item.title = bindings.get("title").value();
        item.citation = bindings.get("cite").value();
        return item;
    }

}
