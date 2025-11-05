package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.api.responses.meta.Provision;

import java.util.List;

public class ExtendedMetadata extends DocumentMetadata {

    @JsonProperty
    @Schema(description = "provisions that confer power to make secondary legislation")
    public List<Provision> confersPower;

    @JsonProperty
    @Schema(description = "provisions containing an amendment that affects the legislation in general")
    public List<Provision> blanketAmendments;

}
