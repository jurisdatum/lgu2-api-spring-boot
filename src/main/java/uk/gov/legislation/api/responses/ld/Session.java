package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.LocalDate;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session {

    @JsonProperty
    public URI uri;

    @JsonProperty
    public String label;

    @JsonProperty
    @Schema(nullable = true)
    public String description;

    @JsonProperty
    public String legislature;

    @JsonProperty
    public LocalDate startDate;

    @JsonProperty
    public LocalDate endDate;

    @JsonProperty
    public Integer startRegnalYear;

    @JsonProperty
    public Integer endRegnalYear;

}
