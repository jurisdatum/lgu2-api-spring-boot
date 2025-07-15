package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class DocumentMetadata extends CommonMetadata {

    @Schema()
    public List<Effect> unappliedEffects;

    @Schema(nullable = true)
    public Boolean upToDate;

}
