package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;

public class YearFacets {

    private static SortedMap<Integer, Integer> parseYearCounts(String json) {
        JsonNode tree;
        try {
            tree = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        TreeMap<Integer, Integer> map = new TreeMap<>(Comparator.reverseOrder());
        ObjectNode results = (ObjectNode) tree.get("results");
        ArrayNode bindings = (ArrayNode) results.get("bindings");
        bindings.forEach(jsonNode -> {
            ObjectNode binding = (ObjectNode) jsonNode;
            Integer year = Integer.parseInt(binding.get("year").get("value").textValue());
            Integer count = Integer.parseInt(binding.get("cnt").get("value").textValue());
            map.put(year, count);
        });
        return map;
    }

    static CompletableFuture<SortedMap<Integer, Integer>> fetchYearCounts(DefraLex defra, String baseWhere) {
        String yearWhere = baseWhere + " ?item :year ?year .";
        String yearFacetQuery = FACET_QUERY.formatted(
            "?year",      // facet variable
            yearWhere,    // base pattern + year triple
            "?year"       // group-by variable
        );
        return defra.getSparqlResultsJson(yearFacetQuery)
            .thenApply(HttpResponse::body)
            .thenApply(YearFacets::parseYearCounts);
    }

}
