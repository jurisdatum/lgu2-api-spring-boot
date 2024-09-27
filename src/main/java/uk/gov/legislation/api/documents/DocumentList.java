package uk.gov.legislation.api.documents;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DocumentList {

    @JsonProperty(index = 1)
    public Meta meta();

    @JsonProperty(index = 2)
    public List<? extends Document> documents();

    @Schema(name = "ListMetadata")
    public interface Meta {

        @JsonProperty(index = 1)
        public int page();

        @JsonProperty(index = 2)
        public int pageSize();

        @JsonProperty(index = 3)
        public int totalPages();

        @JsonProperty(index = 4)
        public LocalDateTime updated();

        @JsonProperty(index = 5)
        public Counts counts();

    }

    public interface Counts {

        @JsonProperty(index = 1)
        public int total();

        @JsonProperty(index = 2)
        public List<? extends Yearly> yearly();

    }

    public interface Yearly {

        @JsonProperty(index = 1)
        public int year();

        @JsonProperty(index = 2)
        public int count();

    }

    public interface Document {

        @JsonProperty(index = 1)
        public String id();

        @JsonProperty(index = 2)
        public String longType();

        @JsonProperty(index = 3)
        public String year();

        @JsonProperty(index = 4)
        public String number();

        @JsonProperty(index = 5)
        public String title();

        @JsonProperty(index = 6)
        public LocalDate created();

        @JsonProperty(index = 7)
        public String version();

    }

}
