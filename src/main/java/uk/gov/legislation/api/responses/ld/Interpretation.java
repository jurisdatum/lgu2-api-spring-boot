package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public class Interpretation {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String language;

    @JsonProperty
    public String shortTitle;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String longTitle;

    @JsonProperty
    public boolean original;

    @JsonProperty
    public boolean current;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public URI parent;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<URI> children;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Item item;

}
