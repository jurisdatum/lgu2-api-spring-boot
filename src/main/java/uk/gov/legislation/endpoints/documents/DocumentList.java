package uk.gov.legislation.endpoints.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public interface DocumentList {

    @JsonProperty(index = 1)
    Meta meta();

    @JsonProperty(index = 2)
    List<? extends Document> documents();

    @Schema(name = "ListMetadata")
    interface Meta {

        @JsonProperty(index = 1)
        int page();

        @JsonProperty(index = 2)
        int pageSize();

        @JsonProperty(index = 3)
        int totalPages();

        @JsonProperty(index = 4)
        LocalDateTime updated();

        @JsonProperty(index = 5)
        Counts counts();

        @JsonProperty(index = 6)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Collection<String> subjects();

    }

    interface Counts {

        @JsonProperty(index = 1)
        int total();

        @JsonProperty(index = 2)
        List<? extends ByType> byType();

        @JsonProperty(index = 3)
        List<? extends ByYear> byYear();

        @JsonProperty(index = 4)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<? extends ByInitial> bySubjectInitial();

    }

    interface ByType {

        @JsonProperty(index = 1)
        String type();

        @JsonProperty(index = 2)
        int count();

    }

    interface ByYear {

        @JsonProperty(index = 1)
        int year();

        @JsonProperty(index = 2)
        int count();

    }

    interface ByInitial {

        @JsonProperty(index = 1)
        String initial();

        @JsonProperty(index = 2)
        int count();

    }

    interface Document {

        @JsonProperty(index = 1)
        String id();

        @JsonProperty(index = 2)
        String longType();

        @JsonProperty(index = 3)
        int year();

        @JsonProperty(index = 4)
        int number();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<? extends AltNumber> altNumbers();

        interface AltNumber {

            @JsonProperty(index = 1)
            @Schema(example = "C", allowableValues = { "C", "L", "S", "NI", "W", "Cy", "Regnal" })
            String category();

            @JsonProperty(index = 2)
            @Schema(example = "1")
            String value();

        }

        @JsonProperty(index = 6)
        String cite();

        @JsonProperty(index = 7)
        String title();

        @JsonProperty(index = 8)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String altTitle();

        @JsonProperty(index = 9)
        ZonedDateTime published();

        @JsonProperty(index = 10)
        ZonedDateTime updated();

        @JsonProperty(index = 11)
        String version();

        @JsonProperty(index = 12)
        @Schema(allowableValues = { "xml", "pdf" }, example = "[\"xml\", \"pdf\"]")
        List<String> formats();

    }

}
