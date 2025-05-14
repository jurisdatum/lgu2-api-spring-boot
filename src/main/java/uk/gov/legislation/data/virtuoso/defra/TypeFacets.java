package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

import static uk.gov.legislation.data.virtuoso.defra.DefraLex.FACET_QUERY;

public class TypeFacets {

    public record TypeCount(String type, String label, int count) {}

    private static List<TypeCount> parseTypeCounts(String json) {
        JsonNode tree;
        try {
            tree = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ArrayNode bindings = (ArrayNode) tree.get("results").get("bindings");
        return StreamSupport.stream(bindings.spliterator(), false)
            .map(binding -> {
                String type = binding.get("type").get("value").textValue();
                type = type.substring(type.lastIndexOf('/') + 1);
                return new TypeCount(
                    type,
                    binding.get("typeLabel").get("value").textValue(),
                    Integer.parseInt(binding.get("cnt").get("value").textValue())
                );
            })
            .sorted(Comparator.comparingInt(TypeCount::count).reversed())
            .toList();
    }

    static CompletableFuture<List<TypeCount>> fetchTypeCounts(DefraLex defra, String baseWhere) {
        String typeWhere = baseWhere +
            " ?item <http://defra-lex.legislation.gov.uk/def/type> ?type ." +
            " ?type  rdfs:label  ?typeLabel .";
        String typeFacetQuery = FACET_QUERY.formatted(
            "?type ?typeLabel",      // facet variable
            typeWhere,    // base pattern + year triple
            "?type ?typeLabel"       // group-by variable
        );
        return defra.getSparqlResultsJson(typeFacetQuery)
            .thenApply(HttpResponse::body)
            .thenApply(TypeFacets::parseTypeCounts);
    }

}
