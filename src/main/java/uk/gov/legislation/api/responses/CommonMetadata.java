package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Extent;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public abstract class CommonMetadata {

    @Schema(example = "ukpga/2024/1")
    public String id;

    @Schema(example = "UnitedKingdomPublicGeneralAct")
    public String longType;

    @Schema(allowableValues = { "ukpga" }, example = "ukpga")
    public String shortType;

    @Schema(example = "2014")
    public int year;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String regnalYear;

    @Schema(example = "1")
    public Integer number;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<AltNumber> altNumbers;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String isbn;

    @Schema(example = "2024-01-25")
    public LocalDate date;

    @Schema(example = "2024 c. 1")
    public String cite;

    @Schema(example = "enacted", examples = { "enacted", "2024-01-25" })
    public String version;

    @Schema(allowableValues = { "final", "revised" }, example = "final")
    public String status;

    @Schema(example = "Post Office (Horizon System) Compensation Act 2024")
    public String title;

    @Schema(allowableValues = { "E", "W", "S", "NI", "EU" })
    public Set<Extent> extent;

    @Schema
    public String lang;

    @Schema(allowableValues = { "King's Printer of Acts of Parliament", "Queen's Printer of Acts of Parliament", "Statute Law Database" }, example = "Statute Law Database")
    public String publisher;

    @Schema(example = "2024-09-23")
    public LocalDate modified;

    @Schema(example = "[ \"enacted\", \"2024-01-25\" ]")
    public SortedSet<String> versions;

    @JsonProperty
    public Has has = new Has();

    @Schema(deprecated = true)
    public boolean schedules;

    @Schema(allowableValues = { "xml", "pdf" }, example = "[\"xml\", \"pdf\"]")
    public List<String> formats;


    public static class AltNumber {

        @Schema(example = "C", allowableValues = { "C", "L", "S", "NI", "W", "Cy", "Regnal" })
        public String category;

        @Schema(example = "1")
        public String value;

    }

    public static class Has {

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public boolean introduction;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public boolean signature;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public boolean schedules;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public boolean note;

    }

}
