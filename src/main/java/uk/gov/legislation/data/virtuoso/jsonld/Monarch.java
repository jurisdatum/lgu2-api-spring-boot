package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;

public class Monarch {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
    public URI type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String regnalName;

    @JsonProperty
    public Integer regnalNumber;

    public static Monarch convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Monarch.class);
    }
}
