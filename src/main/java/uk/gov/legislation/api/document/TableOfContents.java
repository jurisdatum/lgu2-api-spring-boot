package uk.gov.legislation.api.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public interface TableOfContents {

    @JsonProperty(index = 1)
    Metadata meta();

    @JsonProperty(index = 2)
    Contents contents();

    interface Contents {

        @JsonProperty(index = 1)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String title();

        @JsonProperty(index = 2)
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<? extends Item> body();

        @JsonProperty(index = 3)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<? extends Item> appendices();

        @JsonProperty(index = 4)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<? extends Item> attachmentsBeforeSchedules();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<? extends Item> schedules();

        @JsonProperty(index = 6)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<? extends Item> attachments();

    }

    @Schema(name = "ContentsItem")
    interface Item {

        @JsonProperty(index = 1)
        @Schema(allowableValues = { "group","part","chapter","pblock","psubblock","title","section","division","appendix","schedule","attachment","item" }, example = "part")
        String name();

        @JsonProperty(index = 2)
        String number();

        @JsonProperty(index = 3)
        String title();

        @JsonProperty(index = 4)
        @Schema(example = "section-1")
        String ref();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<? extends Item> children();

    }

}
