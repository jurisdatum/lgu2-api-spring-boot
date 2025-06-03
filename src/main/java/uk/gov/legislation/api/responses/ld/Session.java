package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Session {

    @JsonProperty
    public String uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public String description;

    @JsonProperty
    public String sessionOf;
}
