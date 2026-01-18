package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import uk.gov.legislation.data.virtuoso.JsonResults;

import java.util.Optional;

public class Graph {

    public static final ObjectMapper mapper = JsonResults.MAPPER;

    public static ArrayNode extract(String json) throws JsonProcessingException {
        JsonNode tree = mapper.readTree(json);
        return (ArrayNode) tree.get("@graph");
    }

    public static <T> Optional<T> extractFirstObject(String json, Class<T> toValueType) throws JsonProcessingException {
        JsonNode tree = mapper.readTree(json);
        return Optional.ofNullable(tree.get("@graph"))
            .map(ArrayNode.class::cast)
            .filter(graph -> !graph.isEmpty())
            .map(graph -> graph.get(0))
//            .map(ObjectNode.class::cast)
            .map(obj -> mapper.convertValue(obj, toValueType));
    }

}
