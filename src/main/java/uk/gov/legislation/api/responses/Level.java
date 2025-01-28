package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

public class Level {

    @Schema(example = "P1")
    public String name;

    @Schema(example = "section-91")
    public String id;

    @Schema(example = "ukpga/2000/8/section/91")
    public String href;

    @Schema(example = "91")
    public String number;

    @Schema(example = "Penalties for breach of Part 6 rules")
    public String title;

    @Schema(example = "Section 91")
    public String label;

}
