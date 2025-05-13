package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class RegnalLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public Integer endCalendarYear;

    @JsonProperty
    public Integer startCalendarYear;

    @JsonProperty
    public Integer yearOfReign;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public String reign;

    @JsonProperty
    public List<Integer> overlapsCalendarYear;

    @JsonProperty
    public LocalDate startDate;

}



