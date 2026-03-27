package uk.gov.legislation.data.virtuoso.defra;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

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
        } catch (JacksonException e) {
            throw new CompletionException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        ObjectNode binding = (ObjectNode) bindings.get(0);
        return Integer.parseInt(LabeledFacets.getBinding(binding, "cnt"));
    }

}
