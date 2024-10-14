package uk.gov.legislation.api.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public interface TypesForCountry {

    @JsonProperty(index = 1)
    @Schema(allowableValues = { "UK" })
    public String country();

    @JsonProperty(index = 2)
    @Schema(description = "types that exclusively or primarily apply to the country")
    public List<TypeWrapper> primarily();

    @JsonProperty(index = 3)
    @Schema(description = "types that may contain legislation that applies to the country")
    public List<TypeWrapper> possibly();

}
