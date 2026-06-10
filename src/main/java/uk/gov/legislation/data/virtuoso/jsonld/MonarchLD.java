package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import tools.jackson.databind.node.ObjectNode;

public class MonarchLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty public String label;

    @JsonProperty public String regnalName;

    @JsonProperty public Integer regnalNumber;

    public static MonarchLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, MonarchLD.class);
    }
}
