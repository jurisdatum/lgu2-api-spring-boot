package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

public class RichText {

    public static class Node {

        @Schema(allowableValues = { "text", "link" })
        public String type;

        @Schema
        public String text;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(example = "section-1")
        public String id;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(example = "ukpga/2024/1/section/1")
        public String href;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(nullable = true, defaultValue = "false")
        public Boolean missing;

    }

}
