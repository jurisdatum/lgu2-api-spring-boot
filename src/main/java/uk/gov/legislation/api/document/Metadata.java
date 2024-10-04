package uk.gov.legislation.api.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.api.documents.DocumentList;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String regnalYear();

    @JsonProperty(index = 6)
    @Schema(example = "1")
    public int number();

    @JsonProperty(index = 7)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<? extends DocumentList.Document.AltNumber> altNumbers();

    @JsonProperty(index = 8)
    @Schema(example = "2024-01-25")
    public LocalDate date();

    @JsonProperty(index = 9)
    @Schema(example = "2024 c. 1")
    public String cite();

    @JsonProperty(index = 10)
    @Schema(example = "enacted", examples = { "enacted", "2024-01-25" })
    public String version();

    @JsonProperty(index = 11)
    @Schema(allowableValues = { "final", "revised" }, example = "final")
    public String status();

    @JsonProperty(index = 12)
    @Schema(example = "Post Office (Horizon System) Compensation Act 2024")
    public String title();

    @JsonProperty(index = 13)
    @Schema()
    public String lang();

    @JsonProperty(index = 14)
    @Schema(allowableValues = { "King's Printer of Acts of Parliament", "Queen's Printer of Acts of Parliament", "Statute Law Database" }, example = "Statute Law Database")
    public String publisher();

    @JsonProperty(index = 15)
    @Schema(example = "2024-09-23")
    public LocalDate modified();

    @JsonProperty(index = 16)
    @Schema(example = "[ \"enacted\", \"2024-01-25\" ]")
    public List<String> versions();

    @JsonProperty(index = 17)
    @Schema(example = "section-2")
    public String fragment();

    @JsonProperty(value = "prev", index = 18)
    @Schema(example = "section-1")
    public String prev();

    @JsonProperty(value = "next", index = 19)
    @Schema(example = "section-3")
    public String next();

    @JsonProperty(value="schedules", index = 20)
    public boolean schedules();

}
