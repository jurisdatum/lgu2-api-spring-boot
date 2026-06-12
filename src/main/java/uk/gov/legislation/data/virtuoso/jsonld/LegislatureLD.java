package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import tools.jackson.databind.node.ObjectNode;

public class LegislatureLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    public static LegislatureLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, LegislatureLD.class);
    }
}
