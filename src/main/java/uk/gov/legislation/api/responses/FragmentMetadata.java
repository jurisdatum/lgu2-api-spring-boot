package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.endpoints.document.responses.UnappliedEffect;

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
    public List<Level> ancestors;

    @Schema
    public List<Level> descendants;

    @Schema
    public Effects unappliedEffects;

    public static class Effects {

        @Schema(description = "directly affecting the fragment or one of its descendants")
        public List<UnappliedEffect> fragment;

        @Schema(description = "affecting one of the fragment's ancestors")
        public List<UnappliedEffect> ancestor;
    }

}
