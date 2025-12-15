package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class RegnalYearLD {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
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

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<URI> overlapsCalendarYear;

    @JsonProperty
    public URI reign;

    @JsonProperty
    public URI startDate;

    public static RegnalYearLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, RegnalYearLD.class);
    }

}
