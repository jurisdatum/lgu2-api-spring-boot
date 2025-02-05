package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class FragmentMetadata extends CommonMetadata {

    // ToDo should this be "section-2" (slashes replaced with dashes) ??
    @Schema(example = "section/2")
    public String fragment;

    @Schema(example = "section/1")
    public String prev;

    @Schema(example = "section/3")
    public String next;

    @Schema
    public Level fragmentInfo;

    @Schema
    public List<Level> ancestors;

    @Schema
    public List<Level> descendants;

    @Schema
    public Effects unappliedEffects;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(nullable = true)
    public Boolean upToDate;

    public static class Effects {

        @Schema(description = "directly affecting the fragment or any of its descendants")
        public List<Effect> fragment;

        @Schema(description = "affecting one of the fragment's ancestors")
        public List<Effect> ancestor;
    }

}
