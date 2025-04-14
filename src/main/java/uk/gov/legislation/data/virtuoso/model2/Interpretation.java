package uk.gov.legislation.data.virtuoso.model2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Interpretation {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String language;

    @JsonProperty
    public String longTitle;

    @JsonProperty
    public String shortTitle;

    @JsonProperty
    public boolean original;

    @JsonProperty
    public boolean current;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Item item;

}
