package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.JsonResults;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.model.Interpretation;
import uk.gov.legislation.data.virtuoso.model.Resources;
import uk.gov.legislation.data.virtuoso.rdf.RdfMapper;
import uk.gov.legislation.data.virtuoso.rdf.Statement;
import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class Item {

    private final Virtuoso virtuoso;

    public Item(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    public String makeSparqlQuery(String type, int year, int number) {
        return """
            PREFIX leg: <http://www.legislation.gov.uk/def/legislation/>
            SELECT ?s ?p ?o
            WHERE {
               GRAPH ?g {
                  <http://www.legislation.gov.uk/id/%s/%s/%d> a leg:Item .
                  ?s ?p ?o
               }
            }
            """.formatted(type, year, number);
    }

    public String get(String type, int year, int number, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, number);
        return virtuoso.query(query, format);
    }

    public uk.gov.legislation.data.virtuoso.model.Item get(String type, int year, int number) throws IOException, InterruptedException {
        String json = get(type, year, number, "application/sparql-results+json");
        JsonResults parsed = JsonResults.parse(json);
        List<Statement> triples = triples(parsed);
        if (triples.isEmpty())
            return null;
        Map<URI, Map<URI, List<TypedValue>>> grouped = Statement.groupBySubjectAndPredicate(triples);
        uk.gov.legislation.data.virtuoso.model.Item item = null;
        List<Interpretation> interps = new ArrayList<>();
        RdfMapper mapper = new RdfMapper();
        for (Map.Entry<URI, Map<URI, List<TypedValue>>> entry: grouped.entrySet()) {
            if (Resources.isItem(entry.getValue())) {
                item = mapper.read(entry.getValue(), uk.gov.legislation.data.virtuoso.model.Item.class);
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
