package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import uk.gov.legislation.api.responses.meta.AltFormat;

public class DocumentMetadata extends CommonMetadata {

    @Schema(description = "alternative formats")
    public List<AltFormat> altFormats;

    @Schema public List<Effect> unappliedEffects;

    @Schema(nullable = true)
    public Boolean upToDate;
}
