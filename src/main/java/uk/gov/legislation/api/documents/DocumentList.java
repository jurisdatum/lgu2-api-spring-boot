package uk.gov.legislation.api.documents;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DocumentList {

    @JsonProperty(index = 1)
    @JsonGetter(value = "meta")
    public Meta meta();

    @JsonProperty(index = 2)
    @JsonGetter(value = "documents")
    public List<? extends Document> documents();

    @Schema(name = "ListMetadata")
    public interface Meta {

        @JsonProperty(index = 1)
        @JsonGetter(value = "page")
        public int page();

        @JsonProperty(index = 2)
        @JsonGetter(value = "pageSize")
        public int pageSize();

        @JsonProperty(index = 3)
        @JsonGetter(value = "totalPages")
        public int totalPages();

        @JsonProperty(index = 4)
        @JsonGetter(value = "updated")
        public LocalDateTime updated();

        @JsonProperty(index = 5)
        @JsonGetter(value = "counts")
        public Counts counts();

    }

    public interface Counts {

        @JsonProperty(index = 1)
        @JsonGetter(value = "total")
        public int total();

        @JsonProperty(index = 2)
        @JsonGetter(value = "yearly")
        public List<? extends Yearly> yearly();

    }

    public interface Yearly {

        @JsonProperty(index = 1)
        @JsonGetter(value = "year")
        public int year();

        @JsonProperty(index = 2)
        @JsonGetter(value = "count")
        public int count();

    }

    public interface Document {

        @JsonProperty(index = 1)
        @JsonGetter(value = "id")
        public String id();

        @JsonProperty(index = 2)
        @JsonGetter(value = "longType")
        public String longType();

        @JsonProperty(index = 3)
        @JsonGetter(value = "year")
        public String year();

        @JsonProperty(index = 4)
        @JsonGetter(value = "number")
        public String number();

        @JsonProperty(index = 5)
        @JsonGetter(value = "title")
        public String title();

        @JsonProperty(index = 6)
        @JsonGetter(value = "created")
        public LocalDate created();

        @JsonProperty(index = 7)
        @JsonGetter(value = "version")
        public String version();

    }

}
