package uk.gov.legislation.endpoints.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.endpoints.documents.DocumentList;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "DocumentMetadata")
@SuppressWarnings("unused")
public interface Metadata {

    @JsonProperty(index = 1)
    @Schema(example = "ukpga/2024/1")
    String id();

    @JsonProperty(index = 2)
    @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct" }, example = "UnitedKingdomPublicGeneralAct")
    String longType();

    @JsonProperty(index = 3)
    @Schema(allowableValues = { "ukpga" }, example = "ukpga")
    String shortType();

    @JsonProperty(index = 4)
    @Schema(example = "2014")
    int year();

    @JsonProperty(index = 5)
    @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String regnalYear();

    @JsonProperty(index = 6)
    @Schema(example = "1")
    int number();

    @JsonProperty(index = 7)
    @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<? extends DocumentList.Document.AltNumber> altNumbers();

    @JsonProperty(index = 8)
    @Schema(example = "2024-01-25")
    LocalDate date();

    @JsonProperty(index = 9)
    @Schema(example = "2024 c. 1")
    String cite();

    @JsonProperty(index = 10)
    @Schema(example = "enacted", examples = { "enacted", "2024-01-25" })
    String version();

    @JsonProperty(index = 11)
    @Schema(allowableValues = { "final", "revised" }, example = "final") // ToDo "draft"
    String status();

    @JsonProperty(index = 12)
    @Schema(example = "Post Office (Horizon System) Compensation Act 2024")
    String title();

    @JsonProperty(index = 13)
    @Schema()
    String lang();

    @JsonProperty(index = 14)
    @Schema(allowableValues = { "King's Printer of Acts of Parliament", "Queen's Printer of Acts of Parliament", "Statute Law Database" }, example = "Statute Law Database")
    String publisher();

    @JsonProperty(index = 15)
    @Schema(example = "2024-09-23")
    LocalDate modified();

    @JsonProperty(index = 16)
    @Schema(example = "[ \"enacted\", \"2024-01-25\" ]")
    List<String> versions();

    @JsonProperty(value="schedules", index = 17)
    boolean schedules();

    @JsonProperty(index = 18)
    @Schema(allowableValues = { "xml", "pdf" }, example = "[\"xml\", \"pdf\"]")
    List<String> formats();

    @JsonProperty(index = 19)
    @JsonInclude(JsonInclude.Include.ALWAYS)
    List<Effect> unappliedEffects();

    @JsonProperty(index = 20)
    @Schema(example = "section/2", requiredMode = RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String fragment();

    @JsonProperty(value = "prev", index = 21)
    @Schema(example = "section/1", requiredMode = RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String prev();

    @JsonProperty(value = "next", index = 22)
    @Schema(example = "section/3", requiredMode = RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String next();

}
