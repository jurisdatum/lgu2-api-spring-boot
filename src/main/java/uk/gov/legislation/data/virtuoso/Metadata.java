package uk.gov.legislation.data.virtuoso;

import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.model.*;
import uk.gov.legislation.data.virtuoso.queries.SparqlQueries;
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

    private final SparqlQueries queries;

    public Metadata(SparqlQueries queries) {
        this.queries = queries;
    }

    public List<MetadataItem> getListOfMetadata(String type, int year, int limit, int offset) throws IOException, InterruptedException {
        JsonResults json = queries.getJsonListOfMetadata(type, year, limit, offset);
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

}
