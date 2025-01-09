package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.endpoints.document.responses.Level;

import java.util.List;

public class FragmentMetadata extends DocumentMetadata {

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

}
