package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Monarch {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String label;

    @JsonProperty
    public String name;

    @JsonProperty
    public Integer number;

}
