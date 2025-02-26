package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class DocumentMetadata extends CommonMetadata {

    @Schema()
    public List<Effect> unappliedEffects;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(nullable = true)
    public boolean upToDate;

}
