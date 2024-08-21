package uk.gov.legislation.data.virtuoso;

import uk.gov.legislation.data.virtuoso.model.Interpretation;
import uk.gov.legislation.data.virtuoso.model.Item;
import uk.gov.legislation.data.virtuoso.model.Resources;
import uk.gov.legislation.data.virtuoso.rdf.RdfMapper;
import uk.gov.legislation.data.virtuoso.rdf.Statement;
import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Metadata {

    public static Item get(String type, int year, int number) throws IOException, InterruptedException {
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
        item.interpretations = interps;
        return item;
    }

    private static JsonResults getJson(String type, int year, int number) throws IOException, InterruptedException {
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
        JsonResults results = Virtuoso.query(query);
        return results;
    }

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
