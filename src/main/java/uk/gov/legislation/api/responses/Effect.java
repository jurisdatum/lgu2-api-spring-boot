package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.endpoints.document.responses.RichText;

import java.time.LocalDate;
import java.util.List;

public class Effect {

    @Schema
    public boolean applied;

    @Schema
    public boolean required;

    @Schema
    public String type;

    @Schema
    public Source target;

    @Schema
    public Source source;

    @Schema(nullable = true)
    public Provisions commencementAuthority;

    @Schema
    public List<InForce> inForceDates;

    /* */

    public static class Source {

        @Schema(example = "ukpga/2024/1")
        public String id;

        @Schema
        public String longType;

        @Schema
        public int year;

        @Schema
        public int number;

        @Schema
        public String title;

        @Schema(example = "2024 c. 1")
        public String cite;

        @Schema
        public Provisions provisions;

    }

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

}
