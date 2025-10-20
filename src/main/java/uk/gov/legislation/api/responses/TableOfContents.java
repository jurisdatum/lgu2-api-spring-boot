package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.util.Extent;

import java.util.List;
import java.util.Set;

public class TableOfContents {

    @Schema
    public DocumentMetadata meta;

    @Schema
    public Contents contents;

    public static class Contents {

        @Schema
        public String title;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Item introduction;

        @Schema
        public List<Item> body;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Item signature;

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

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Item explanatoryNote;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Item earlierOrders;

    }

    @Schema(name = "ContentsItem")
    public static class Item {

        @Schema(allowableValues = { "group","part","chapter","pblock","psubblock","title","section","division","appendix","schedule","attachment","item" }, example = "part")
        public String name;

        @Schema
        public String number;

        @Schema
        public String title;

        @Schema(example = "crossheading-final-provisions", deprecated = true, description = "use href")
        public String ref;

        @Schema(example = "crossheading-final-provisions")
        public String id;

        @Schema(example = "crossheading/final-provisions")
        public String href;

        @JsonProperty
        public Set<Extent> extent;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> children;

    }

    public static class Introduction extends Item {

        public Introduction() {
            name = "introduction";
            title = "Introductory Text";
            ref = "introduction";
        }

    }

    public static class Signature extends Item {

        public Signature() {
            name = "signature";
            title = "Signature";
            ref = "signature";
        }

    }

    public static class ExplanatoryNote extends Item {

        public ExplanatoryNote() {
            name = "explanatoryNote";
            title = "Explanatory Note";
            ref = "note";
        }

    }

    public static class EarlierOrders extends Item {

        public EarlierOrders() {
            name = "earlierOrders";
            title = "Note as to Earlier Commencement Orders";
            ref = "earlier-orders";
        }

    }

}
