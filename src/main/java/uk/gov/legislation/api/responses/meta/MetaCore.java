package uk.gov.legislation.api.responses.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

public class MetaCore {

    @Schema
    public String id;

    @Schema
    public String longType;

    @Schema
    public String shortType;

    @Schema
    public int year;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String regnalYear;

    @Schema
    public long number;

}
