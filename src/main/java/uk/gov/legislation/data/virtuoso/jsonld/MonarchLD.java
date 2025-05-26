package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;

public class MonarchLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String regnalName;

    @JsonProperty
    public Integer regnalNumber;

    public static MonarchLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, MonarchLD.class);
    }
}
