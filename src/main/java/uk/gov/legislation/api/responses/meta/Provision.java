package uk.gov.legislation.api.responses.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Provision {

    @JsonProperty
    public String id;

    @JsonProperty
    public String href;

    @JsonProperty
    public String label;

    @JsonProperty
    public String title;

}
