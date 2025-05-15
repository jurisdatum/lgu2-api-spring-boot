package uk.gov.legislation.endpoints.ld.monarch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class MonarchLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String regnalName;

    @JsonProperty
    public Integer regnalNumber;
}
