package uk.gov.legislation.data.virtuoso.jsonld;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import uk.gov.legislation.data.virtuoso.JsonResults;

import java.util.Optional;

public class Graph {

    public static final ObjectMapper mapper = JsonResults.MAPPER;

    public static ArrayNode extract(String json) throws JacksonException {
        JsonNode tree = mapper.readTree(json);
        return (ArrayNode) tree.get("@graph");
    }

    public static <T> Optional<T> extractFirstObject(String json, Class<T> toValueType) throws JacksonException {
        JsonNode tree = mapper.readTree(json);
        return Optional.ofNullable(tree.get("@graph"))
            .map(ArrayNode.class::cast)
            .filter(graph -> !graph.isEmpty())
            .map(graph -> graph.get(0))
//            .map(ObjectNode.class::cast)
            .map(obj -> mapper.convertValue(obj, toValueType));
    }

}
