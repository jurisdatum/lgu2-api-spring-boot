package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class Reign {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String label;

    @JsonProperty
    public List<String> monarchs;

    @JsonProperty
    public LocalDate startDate;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public Integer startRegnalYear;

    @JsonProperty
    public Integer endRegnalYear;

}
