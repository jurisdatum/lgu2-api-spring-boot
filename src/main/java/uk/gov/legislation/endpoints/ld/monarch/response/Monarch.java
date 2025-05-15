package uk.gov.legislation.endpoints.ld.monarch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Monarch {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String name;

    @JsonProperty
    public Integer number;

    public Monarch() {
    }
}