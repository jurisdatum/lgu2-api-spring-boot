package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ValueAndType {

    @JsonProperty("value")
    @JsonAlias("@value")
    public String value;

    @JsonProperty("type")
    @JsonAlias("@type")
    public String type;

    // Defensive deserializer for RDF data quality issues where strings are sometimes
    // unnecessarily typed as xsd:string. Handles both plain "eng" and typed
    // "eng"^^xsd:string literals by normalizing to simple Strings.
    static class DefensiveStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node.isTextual())
                return node.asText();
            return Graph.mapper.convertValue(node, ValueAndType.class).value;
        }
    }

}
