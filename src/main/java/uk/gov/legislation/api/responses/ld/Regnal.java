package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDate;

public class Regnal {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String label;

    @JsonProperty
    public String reign;

    @JsonProperty
    public Integer yearOfReign;

    @JsonProperty
    public LocalDate startDate;

    @JsonProperty
    public LocalDate endDate;

}


