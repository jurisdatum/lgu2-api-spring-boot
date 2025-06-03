package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SessionLD {

    @JsonProperty("@id")
    public String id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String comment;

    @JsonProperty
    public String sessionOf;

    public static SessionLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, SessionLD.class);
    }

}

