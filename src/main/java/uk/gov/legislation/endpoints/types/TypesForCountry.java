package uk.gov.legislation.endpoints.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class TypesForCountry {

    @JsonProperty(index = 1)
    @Schema(allowableValues = {"UK", "WALES", "SCOTLAND", "NORTHERN_IRELAND"})
    private String country;

    @JsonProperty(index = 2)
    @Schema(description = "Types that exclusively or primarily apply to the country")
    private List <TypeWrapper> primarily;

    @JsonProperty(index = 3)
    @Schema(description = "Types that may contain legislation that applies to the country")
    private List<TypeWrapper> possibly;

    public TypesForCountry(String country, List<TypeWrapper> primarily, List<TypeWrapper> possibly) {
        this.country = country;
        this.primarily = primarily;
        this.possibly = possibly;
    }

}

