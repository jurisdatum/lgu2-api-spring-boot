package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class Reign {

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
    public URI endRegnalYear;

    @JsonProperty
    public URI startCalendarYear;

    @JsonProperty
    public URI startRegnalYear;

    @JsonProperty
    public URI endDate;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<URI> monarch;

    @JsonProperty
    public List<URI> overlapsCalendarYear;

    @JsonProperty
    public List<URI> overlapsRegnalYear;

    @JsonProperty
    public URI startDate;

    public static Reign convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Reign.class);
    }

}
