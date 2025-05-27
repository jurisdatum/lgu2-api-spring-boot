package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Total {

    static CompletableFuture<Integer> get(DefraLex defra, String where) {
        final String query = "SELECT (COUNT(DISTINCT ?item) AS ?cnt) WHERE { %s }"
            .formatted(where);
        return defra.getSparqlResultsJson(query)
            .thenApply(Total::parse);
    }

    private static Integer parse(String json) {
        JsonNode tree;
        try {
            tree = DefraLex.mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        ObjectNode binding = (ObjectNode) bindings.get(0);
        return Integer.parseInt(LabeledFacets.getBinding(binding, "cnt"));
    }

}
