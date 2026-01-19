package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ClassLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    public final Map<String, Object> other = new LinkedHashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        if ("subClassOf".equals(key) && value instanceof String s)
            value = List.of(s);
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
