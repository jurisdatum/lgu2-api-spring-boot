package uk.gov.legislation.data.virtuoso;

import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.model.*;
import uk.gov.legislation.data.virtuoso.rdf.RdfMapper;
import uk.gov.legislation.data.virtuoso.rdf.Statement;
import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class Metadata {

    private final Virtuoso virtuoso;

    public Metadata(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    /**
     * Retrieves an Item based on type, year, and number.
     * Fetches RDF data, processes statements, and maps them to an Item object.
     */
    public  Item get(String type, int year, int number) throws IOException, InterruptedException {
        JsonResults json = getJson(type, year, number);
        List<Statement> triples = triples(json);
        if (triples.isEmpty())
            return null;
        Map<URI, Map<URI, List<TypedValue>>> grouped = Statement.groupBySubjectAndPredicate(triples);
        Item item = null;
        List<Interpretation> interps = new ArrayList<>();
        RdfMapper mapper = new RdfMapper();
        for (Map.Entry<URI, Map<URI, List<TypedValue>>> entry: grouped.entrySet()) {
            if (Resources.isItem(entry.getValue())) {
                item = mapper.read(entry.getValue(), Item.class);
                item.uri = entry.getKey();
            }
            if (Resources.isInterpretation(entry.getValue())) {
                Interpretation interp = mapper.read(entry.getValue(), Interpretation.class);
                interp.uri = entry.getKey();
                interps.add(interp);
            }
        }
        assert item != null;
        item.interpretations = interps;
        return item;
    }


    /**
     * Create Query for getting single Metadata Item
     */
    private JsonResults getJson(String type, int year, int number) throws IOException, InterruptedException {
        String query = """
            PREFIX leg: <http://www.legislation.gov.uk/def/legislation/>
            SELECT ?s ?p ?o
            WHERE {
               GRAPH ?g {
                  <http://www.legislation.gov.uk/id/%s/%s/%d> a leg:Item .
                  ?s ?p ?o
               }
            }
            """.formatted(type, year, number);
        return virtuoso.query(query);
    }


    public List<MetadataItem> getListOfMetadata(String type, int year, int limit, int offset) throws IOException, InterruptedException {
        JsonResults json = getJsonListOfMetadata(type, year, limit, offset);
        List<Statement> triples = triple(json);

        if (triples.isEmpty()) return Collections.emptyList();

        Map<URI, Map<URI, List<TypedValue>>> grouped = Statement.groupBySubjectAndPredicate(triples);
        RdfMapper mapper = new RdfMapper();
        List<MetadataItem> items = new ArrayList<>();

        for (Map.Entry<URI, Map<URI, List<TypedValue>>> entry : grouped.entrySet()) {
            MetadataItem item = mapper.read(entry.getValue(), MetadataItem.class);

            // Created a new MetadataItem object with only the required fields
            MetadataItem filteredItem = new MetadataItem();
            filteredItem.number = item.number;
            filteredItem.year = item.year;
            filteredItem.citation = item.citation;
            filteredItem.title = item.title;
            items.add(filteredItem);
        }
        return items;
    }

    /**
     * Constructs SPARQL query to fetch JSON results for metadata Item List.
     */
    private JsonResults getJsonListOfMetadata(String type, int year, int limit, int offset) throws IOException, InterruptedException {
        String query = """
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
        return virtuoso.query(query);
    }

    /**
     * Converts JSON results into a list of RDF statements.
     */
    private static List<Statement> triple(JsonResults json) {
        String baseUri = "http://www.legislation.gov.uk/def/legislation/";
        List<Statement> statements = new ArrayList<>();

        for (Map <String, JsonResults.Value> binding : json.results.bindings) {
            URI subject = URI.create(binding.get("item").value);

            List<Map.Entry<String, String>> fields = List.of(
                    Map.entry("title", "title"),
                    Map.entry("year", "year"),
                    Map.entry("number", "number"),
                    Map.entry("cite", "citation")
            );

            for (Map.Entry<String, String> entry : fields) {
                String jsonKey = entry.getKey();
                String rdfProperty = entry.getValue();

                if (binding.containsKey(jsonKey)) {
                    statements.add(new Statement(
                            subject,
                            URI.create(baseUri + rdfProperty),
                            binding.get(jsonKey)
                    ));
                }
            }
        }

        return statements;
    }

    /**
     * Converts JSON results into a list of RDF statements using iteration.
     */

    private static List<Statement> triples(JsonResults json) {
        List<Statement> triples = new ArrayList<>();
        for (Map<String, JsonResults.Value> binding: json.results.bindings) {
            URI subject = URI.create(binding.get("s").value);
            URI predicate = URI.create(binding.get("p").value);
            JsonResults.Value object = binding.get("o");
            Statement triple = new Statement(subject, predicate, object);
            triples.add(triple);
        }
        return triples;
    }

}
