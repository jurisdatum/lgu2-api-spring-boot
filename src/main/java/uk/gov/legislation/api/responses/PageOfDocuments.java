package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.endpoints.search.SearchParameters;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public class PageOfDocuments {

    @Schema
    public Meta meta;

    @Schema
    public List<Document> documents;

    @Schema(name = "ListMetadata")
    public static class Meta {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public SearchParameters query;

        @Schema
        public int page;

        @Schema
        public int pageSize;

        @Schema
        public int totalPages;

        @Schema
        public ZonedDateTime updated;

        @Schema
        public Counts counts;

        @Schema
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Collection<String> subjects;

    }

    public static class Counts {

        @Schema
        public int total;

        @Schema
        public List<ByType> byType;

        @Schema
        public List<ByYear> byYear;

        @Schema
        public List<ByInitial> bySubjectInitial;

    }

    public static class ByType {

        @Schema
        public String type;

        @Schema
        public int count;

    }

    public static class ByYear {

        @Schema
        public int year;

        @Schema
        public int count;

    }

    public static class ByInitial {

        @Schema
        public String initial;

        @Schema
        public int count;

    }

    public static class Document {

        @Schema
        public String id;

        @Schema
        public String longType;

        @Schema
        public int year;

        @Schema
        public int number;

        @Schema
        // FixMe this should probably have the same @JsonInclude(JsonInclude.Include.NON_EMPTY) as CommonMetadata
        public List<CommonMetadata.AltNumber> altNumbers;

        @Schema
        public String cite;

        @Schema
        public String title;

        @Schema
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String altTitle;

        @Schema
        public ZonedDateTime published;

        @Schema
        public ZonedDateTime updated;

        @Schema
        public String version;

        @Schema(allowableValues = { "xml", "pdf" }, example = "[\"xml\", \"pdf\"]")
        public List<String> formats;

    }

}
