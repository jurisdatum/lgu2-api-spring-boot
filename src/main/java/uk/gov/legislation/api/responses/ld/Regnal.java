package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class Regnal {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public Integer endYear;

    @JsonProperty
    public Integer startYear;

    @JsonProperty
    public Integer yearOfReign;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public String reign;

    @JsonProperty
    public List<Integer> overlappingYears;

    @JsonProperty
    public LocalDate startDate;

}


