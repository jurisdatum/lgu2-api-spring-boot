package uk.gov.legislation.api.responses.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.LocalDate;

public class AltFormat {

    @JsonProperty
    public URI url;

    @JsonProperty
    public String label;

    @JsonProperty
    public LocalDate date;

    @JsonProperty
    public Long size;

    @JsonProperty()
    @Schema(allowableValues = {"English", "Welsh", "Mixed" })
    public String language;

    @JsonProperty
    public String type;

    @JsonProperty
    public String thumbnail;

}
