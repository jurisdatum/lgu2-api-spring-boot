package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.ZonedDateTime;

public class DataSet {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public String title;

    @JsonProperty
    public ZonedDateTime created;

    @JsonProperty
    public ZonedDateTime modified;

}
