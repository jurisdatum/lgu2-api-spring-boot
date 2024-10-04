package uk.gov.legislation.api.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
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

        @JsonProperty(index = 6)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Collection<String> subjects();

    }

    public interface Counts {

        @JsonProperty(index = 1)
        public int total();

        @JsonProperty(index = 2)
        public List<? extends ByType> byType();

        @JsonProperty(index = 3)
        public List<? extends ByYear> byYear();

        @JsonProperty(index = 4)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<? extends ByInitial> bySubjectInitial();

    }

    public interface ByType {

        @JsonProperty(index = 1)
        public String type();

        @JsonProperty(index = 2)
        public int count();

    }

    public interface ByYear {

        @JsonProperty(index = 1)
        public int year();

        @JsonProperty(index = 2)
        public int count();

    }

    public interface ByInitial {

        @JsonProperty(index = 1)
        public String initial();

        @JsonProperty(index = 2)
        public int count();

    }

    public interface Document {

        @JsonProperty(index = 1)
        public String id();

        @JsonProperty(index = 2)
        public String longType();

        @JsonProperty(index = 3)
        public int year();

        @JsonProperty(index = 4)
        public int number();

        @JsonProperty(index = 5)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<? extends AltNumber> altNumbers();

        public static interface AltNumber {

            @JsonProperty(index = 1)
            public String category();

            @JsonProperty(index = 2)
            public String value();

        }

        @JsonProperty(index = 6)
        public String cite();

        @JsonProperty(index = 7)
        public String title();

        @JsonProperty(index = 8)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String altTitle();

        @JsonProperty(index = 9)
        public ZonedDateTime published();

        @JsonProperty(index = 10)
        public ZonedDateTime updated();

        @JsonProperty(index = 11)
        public String version();

    }

}
