package uk.gov.legislation.endpoints.document.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Extent;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Effect {

    @Schema
    public boolean required;

    @Schema
    public String type;

    @Schema
    public Provisions affected;

    @Schema(allowableValues = { "E", "W", "S", "NI", "EU" })
    public Set<Extent> affectedExtent;

    @Schema
    public List<InForce> inForceDates;

    @Schema
    public Source source;

    @Schema
    public Provisions commencement;

    @Schema(nullable = true)
    public String notes;


    public static class Provisions {

        @Schema
        public String plain;

        @Schema
        public List<RichText.Node> rich;

    }

    public static class InForce {

        @Schema(nullable = true)
        public LocalDate date;

        @Schema
        public boolean applied;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(nullable = true)
        public Boolean prospective;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @Schema(nullable = true)
        public String description;

    }

    public static class Source {

        @Schema
        public String id;

        @Schema
        public String longType;

        @Schema
        public int year;

        @Schema
        public int number;

        @Schema
        public Provisions provisions;

    }

}
