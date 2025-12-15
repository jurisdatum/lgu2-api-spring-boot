package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.api.responses.meta.AssocMeta;

public class Associated {

    @Schema
    public AssocMeta meta;

}
