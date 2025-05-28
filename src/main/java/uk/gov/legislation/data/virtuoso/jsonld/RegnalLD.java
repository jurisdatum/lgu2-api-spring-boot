package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class RegnalLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public URI type;

    @JsonProperty
    public String label;

    @JsonProperty
    public URI endCalendarYear;

    @JsonProperty
    public URI startCalendarYear;

    @JsonProperty
    public Integer yearOfReign;

    @JsonProperty
    public URI endDate;

    @JsonProperty
    public List<URI> overlapsCalendarYear;

    @JsonProperty
    public URI reign;

    @JsonProperty
    public URI startDate;

    public static RegnalLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, RegnalLD.class);
    }

}
