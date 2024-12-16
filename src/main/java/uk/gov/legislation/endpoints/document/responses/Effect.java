package uk.gov.legislation.endpoints.document.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class Effect {

    @Schema
    public boolean requiresApplication;

    @Schema
    public String type;

    @Schema
    public List<Section> affectedProvisions;

    @Schema
    public List<InForce> inForceDates;

    @Schema
    public Source source;

    @Schema
    public String notes;

    public static class Section {

        @Schema(example = "section-11")
        public String id;

        @Schema
        public boolean missing;

    }

    public static class InForce {

        @Schema
        public LocalDate date;

        @Schema
        public String qualification;

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

    }

}
