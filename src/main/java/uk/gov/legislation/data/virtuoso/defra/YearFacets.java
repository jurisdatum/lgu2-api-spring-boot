package uk.gov.legislation.data.virtuoso.defra;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.StreamSupport;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;
import static uk.gov.legislation.data.virtuoso.defra.LabeledFacets.getBinding;

public class YearFacets {

    public record Count(int year, int count) { }

    static CompletableFuture<List<Count>> fetch(DefraLex defra, String baseWhere) {
        String prop = "http://www.legislation.gov.uk/def/legislation/year";
        return fetch(defra, baseWhere, prop);
    }

    static CompletableFuture<List<Count>> fetch(DefraLex defra, String baseWhere, String prop) {
        String where = baseWhere + " ?item <" + prop + "> ?year .";
        String query = FACET_QUERY.formatted("?year", where, "?year");
        return defra.getSparqlResultsJson(query)
            .thenApply(YearFacets::parse);
    }

    private static List<Count> parse(String json) {
        JsonNode tree;
        try {
            tree = DefraLex.mapper.readTree(json);
        } catch (JacksonException e) {
            throw new CompletionException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        return StreamSupport.stream(bindings.spliterator(), false)
            .map(YearFacets::map)
            .sorted(Comparator.comparingInt(Count::year).reversed())
            .toList();
    }

    private static Count map(JsonNode binding) {
        int year = Integer.parseInt(getBinding(binding, "year"));
        int count = Integer.parseInt(getBinding(binding, "cnt"));
        return new Count(year, count);
    }

}
