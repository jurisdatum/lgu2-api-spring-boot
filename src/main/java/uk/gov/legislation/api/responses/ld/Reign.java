package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class Reign {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public Integer endYear;

    @JsonProperty
    public Integer endRegnalYear;

    @JsonProperty
    public Integer startYear;

    @JsonProperty
    public Integer startRegnalYear;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public String monarch;

    @JsonProperty
    public List<Integer> overlappingYears;

    @JsonProperty
    public List<Integer> overlappingRegnalYears;

    @JsonProperty
    public LocalDate startDate;


}
