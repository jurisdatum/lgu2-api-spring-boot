package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.endpoints.document.responses.UnappliedEffect;

import java.util.List;

public class DocumentMetadata extends CommonMetadata {

    @Schema()
    public List<UnappliedEffect> unappliedEffects;

}
