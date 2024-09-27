package uk.gov.legislation.api.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "DocumentMetadata")
public interface Metadata {

    @JsonProperty(index = 1)
    @Schema(example = "ukpga/2024/1")
    public String id();

    @JsonProperty(index = 2)
    @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct" }, example = "UnitedKingdomPublicGeneralAct")
    public String longType();

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "ukpga" }, example = "ukpga")
    public String shortType();

    @JsonProperty(index = 4)
    @Schema(example = "2014")
    public int year();

    @JsonProperty(index = 5)
    @Schema()
    public String regnalYear();

    @JsonProperty(index = 6)
    @Schema(example = "1")
    public int number();

    @JsonProperty(index = 7)
    @Schema(example = "2024-01-25")
    public LocalDate date();

    @JsonProperty(index = 8)
    @Schema(example = "2024 c. 1")
    public String cite();

    @JsonProperty(index = 9)
    @Schema(example = "enacted", examples = { "enacted", "2024-01-25" })
    public String version();

    @JsonProperty(index = 10)
    @Schema(allowableValues = { "final", "revised" }, example = "final")
    public String status();

    @JsonProperty(index = 11)
    @Schema(example = "Post Office (Horizon System) Compensation Act 2024")
    public String title();

    @JsonProperty(index = 12)
    @Schema()
    public String lang();

    @JsonProperty(index = 13)
    @Schema(allowableValues = { "King's Printer of Acts of Parliament", "Queen's Printer of Acts of Parliament", "Statute Law Database" }, example = "Statute Law Database")
    public String publisher();

    @JsonProperty(index = 14)
    @Schema(example = "2024-09-23")
    public LocalDate modified();

    @JsonProperty(index = 15)
    @Schema(example = "[ \"enacted\", \"2024-01-25\" ]")
    public List<String> versions();

    @JsonProperty(index = 16)
    public String fragment();

    @JsonProperty(value = "prev", index = 17)
    public String prev();

    @JsonProperty(value = "next", index = 18)
    @Schema(example = "section-2")
    public String next();

    @JsonProperty(value="schedules", index = 19)
    public boolean schedules();

}
