package uk.gov.legislation.api.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public interface TableOfContents {

    @JsonProperty(index = 1)
    public Metadata meta();

    @JsonProperty(index = 2)
    public Contents contents();

    public interface Contents {

        @JsonProperty(index = 1)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        public String title();

        @JsonProperty(index = 2)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        public List<? extends Item> body();

        @JsonProperty(index = 3)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        public List<? extends Item> appendices();

        @JsonProperty(index = 4)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        public List<? extends Item> attachmentsBeforeSchedules();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        public List<? extends Item> schedules();

        @JsonProperty(index = 6)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        public List<? extends Item> attachments();

    }

    @Schema(name = "ContentsItem")
    public interface Item {

        @JsonProperty(index = 1)
        @Schema(allowableValues = { "group","part","chapter","pblock","psubblock","title","section","division","appendix","schedule","attachment","item" }, example = "part")
        public String name();

        @JsonProperty(index = 2)
        public String number();

        @JsonProperty(index = 3)
        public String title();

        @JsonProperty(index = 4)
        @Schema(example = "section-1")
        public String ref();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<? extends Item> children();

    }

}
