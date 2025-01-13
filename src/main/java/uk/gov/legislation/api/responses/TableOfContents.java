package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class TableOfContents {

    @Schema
    public DocumentMetadata meta;

    @Schema
    public Contents contents;

    public static class Contents {

        @Schema
        public String title;

        @Schema
        public List<Item> body;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> appendices;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> attachmentsBeforeSchedules;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> schedules;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> attachments;

    }

    @Schema(name = "ContentsItem")
    public static class Item {

        @Schema(allowableValues = { "group","part","chapter","pblock","psubblock","title","section","division","appendix","schedule","attachment","item" }, example = "part")
        public String name;

        @Schema
        public String number;

        @Schema
        public String title;

        @Schema(example = "section-1")
        public String ref;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> children;

    }

}
