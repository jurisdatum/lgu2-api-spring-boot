package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Graph {

    static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ArrayNode extract(String json) throws JsonProcessingException {
        JsonNode tree = mapper.readTree(json);
        return (ArrayNode) tree.get("@graph");
    }

    public static Stream<ObjectNode> stream(String json) throws JsonProcessingException {
        ArrayNode graph = Graph.extract(json);
        return StreamSupport.stream(graph.spliterator(), false)
            .map(ObjectNode.class::cast);
    }

}
