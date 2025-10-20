package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class Level {

    @Schema(example = "P1")
    public String element;

    @Schema(example = "crossheading-final-provisions")
    public String id;

    @Schema(example = "crossheading/final-provisions")
    public String href;

    @Schema(example = "91")
    public String number;

    @Schema(example = "Penalties for breach of Part 6 rules")
    public String title;

    @Schema(example = "Section 91")
    public String label;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean prospective;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate start;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate end;

}
