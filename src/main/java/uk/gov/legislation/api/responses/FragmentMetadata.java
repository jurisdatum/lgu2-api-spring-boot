package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Metadata describing a specific document fragment and its context.
 *
 * <p>Notes on navigation labels: {@link #prevInfo} and {@link #nextInfo} include a
 * human‑readable {@code label} derived from the Atom {@code link/@title} in the
 * source XML. See {@link LabelledLink}.</p>
 */
public class FragmentMetadata extends CommonMetadata {

    @Schema(example = "section/2", deprecated = true, description = "use fragmentInfo")
    public String fragment;

    @Schema(example = "section/1", deprecated = true, description = "use prevInfo")
    public String prev;

    @Schema(example = "section/3", deprecated = true, description = "use nextInfo")
    public String next;

    @Schema
    public Level fragmentInfo;

    @Schema(description = "Previous fragment with display label (see LabelledLink).")
    public LabelledLink prevInfo;

    @Schema(description = "Next fragment with display label (see LabelledLink).")
    public LabelledLink nextInfo;

    @Schema
    public List<Level> ancestors;

    @Schema
    public List<Level> descendants;

    @Schema
    public Effects unappliedEffects;

    @Schema(nullable = true)
    public Boolean upToDate;

    public static class Effects {

        @Schema(description = "directly affecting the fragment or any of its descendants")
        public List<Effect> fragment;

        @Schema(description = "affecting one of the fragment's ancestors")
        public List<Effect> ancestor;
    }

}
