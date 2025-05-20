package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

class Helper {

    /* for fields that are sometimes an object and sometimes an array */
    static <T> List<T> oneOrMany(JsonNode node, Class<T> t) {
        if (node instanceof ArrayNode)
            return StreamSupport.stream(node.spliterator(), false)
                .map(o -> Graph.mapper.convertValue(o, t))
                .toList();
        return Collections.singletonList(
            Graph.mapper.convertValue(node, t)
        );
    }

}
