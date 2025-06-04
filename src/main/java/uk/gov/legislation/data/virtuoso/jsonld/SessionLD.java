package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;

public class SessionLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public URI type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String comment;

    @JsonProperty
    public URI sessionOf;

    @JsonProperty
    public URI startDate;

    @JsonProperty
    public URI endDate;

    @JsonProperty
    public URI startRegnalYear;

    @JsonProperty
    public URI endRegnalYear;

    public static SessionLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, SessionLD.class);
    }

}
