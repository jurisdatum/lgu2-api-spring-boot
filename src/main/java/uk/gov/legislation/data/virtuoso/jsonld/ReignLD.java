package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

import static uk.gov.legislation.data.virtuoso.jsonld.Helper.oneOrMany;

public class ReignLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

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

    @JsonProperty
    public List<URI> monarch;

    @JsonSetter("monarch")
    public void setMonarchs(JsonNode node) {
        this.monarch = oneOrMany(node, URI.class);
    }

    @JsonProperty
    public List<URI> overlapsCalendarYear;

    @JsonProperty
    public List<URI> overlapsRegnalYear;

    @JsonProperty
    public URI startDate;

    public static ReignLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, ReignLD.class);
    }

}
