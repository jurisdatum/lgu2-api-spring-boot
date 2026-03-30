package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class RegnalYearLD {

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

    public static RegnalYearLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, RegnalYearLD.class);
    }

}
