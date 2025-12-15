package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ClassLD {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<URI> type;

    public final Map<String, Object> other = new LinkedHashMap<>();

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
