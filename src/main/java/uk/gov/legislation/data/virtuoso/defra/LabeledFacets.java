package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.StreamSupport;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;

public class LabeledFacets {

    public record Count(URI uri, String id, String label, int count) { }

    static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";

    static final String PREF_LABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";

    static CompletableFuture<List<Count>> fetch(DefraLex defra, String baseWhere, String prop) {
        return fetch(defra, baseWhere, prop, RDFS_LABEL);
    }
    // prop is the uri of the main property
    // label is the uri of the label property
    static CompletableFuture<List<Count>> fetch(DefraLex defra, String baseWhere, String prop, String label) {
        String where = baseWhere +
            " ?item <" + prop + "> ?x ." +
            " ?x <" + label + "> ?label .";
        String query = FACET_QUERY.formatted("?x ?label", where, "?x ?label");
        return defra.getSparqlResultsJson(query)
            .thenApply(HttpResponse::body)
            .thenApply(LabeledFacets::parse);
    }

    static String getBinding(JsonNode binding, String key) {
        return binding.get(key).get("value").textValue();
    }

    private static List<Count> parse(String json) {
        JsonNode tree;
        try {
            tree = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readTree(json);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        return StreamSupport.stream(bindings.spliterator(), false)
            .map(binding -> {
                URI uri = URI.create(getBinding(binding, "x"));
                String id = uri.toString().substring(uri.toString().lastIndexOf('/') + 1);
                String label = getBinding(binding, "label");
                int count = Integer.parseInt(getBinding(binding, "cnt"));
                return new Count(uri, id, label, count);
            })
            .sorted(Comparator.comparingInt(Count::count).reversed())
            .toList();
    }

}
