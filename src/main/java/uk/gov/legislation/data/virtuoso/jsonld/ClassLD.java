package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ClassLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public URI type;

    public final Map<String, Object> other = new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        other.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> other() {
        return other;
    }

    public static ClassLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, ClassLD.class);
    }

}
