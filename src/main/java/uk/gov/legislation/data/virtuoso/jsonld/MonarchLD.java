package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;

public record MonarchLD(
    @JsonProperty("@id") URI id,
    @JsonProperty("@type") String type,
    @JsonProperty String label,
    @JsonProperty String regnalName,
    @JsonProperty Integer regnalNumber
) {
    public static MonarchLD fromJsonNode(ObjectNode node) {
        return Graph.mapper.convertValue(node, MonarchLD.class);
    }
}