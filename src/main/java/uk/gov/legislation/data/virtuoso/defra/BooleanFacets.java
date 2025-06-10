package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.StreamSupport;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;
import static uk.gov.legislation.data.virtuoso.defra.LabeledFacets.getBinding;

public class BooleanFacets {

    public record Count(Boolean value, int count) { }

    static CompletableFuture<List<Count>> fetch(DefraLex defra, String baseWhere, String prop) {
        String where = baseWhere + " ?item <" + prop + "> ?value .";
        String query = FACET_QUERY.formatted("?value", where, "?value");
        return defra.getSparqlResultsJson(query)
            .thenApply(BooleanFacets::parse);
    }

    private static List<Count> parse(String json) {
        JsonNode tree;
        try {
            tree = DefraLex.mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        return StreamSupport.stream(bindings.spliterator(), false)
            .map(BooleanFacets::map)
            .sorted(Comparator.comparing(Count::value).reversed())
            .toList();
    }

    private static Count map(JsonNode binding) {
        Boolean value = "1".equals(getBinding(binding, "value"));
        int count = Integer.parseInt(getBinding(binding, "cnt"));
        return new Count(value, count);
    }

}
