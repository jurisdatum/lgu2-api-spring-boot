package uk.gov.legislation.api.responses.meta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class AssocMeta extends MetaCore {

    @Schema
    public String title;

    @Schema
    public LocalDate modified;

    @Schema
    public MetaCore associatedWith;

    @Schema
    public String stage;

    @Schema
    public String department;

    @Schema(description = "alternative formats")
    public List<AltFormat> altFormats;

}
