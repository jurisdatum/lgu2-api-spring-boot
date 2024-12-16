package uk.gov.legislation.endpoints.fragment.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class FragmentMetadata {

    @Schema(example = "section/2")
    public String id;

    @Schema(example = "Section 2")
    public String label;

    @Schema(example = "section/1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String prev;

    @Schema(example = "section/3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    public String next;

    @Schema
    public List<Provision> ancestors;

    public static class Provision {

        @Schema
        public String id;

        @Schema
        public String label;

    }

}
