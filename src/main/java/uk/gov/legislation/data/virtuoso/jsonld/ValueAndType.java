package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class ValueAndType {

    @JsonProperty("@value")
    public String value;

    @JsonProperty("@type")
    public String type;

    static ValueAndType convert(JsonNode node) {
        if (node instanceof TextNode) {
            ValueAndType value = new ValueAndType();
            value.value = node.asText();
            value.type = "http://www.w3.org/2001/XMLSchema#string";
            return value;
        }
        return Graph.mapper.convertValue(node, ValueAndType.class);
    }

}
