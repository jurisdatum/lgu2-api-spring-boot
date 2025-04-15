package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PageOfItems {

    @JsonProperty
    public Meta meta;

    @JsonProperty
    public List<Item> items;

    public static class Meta {

        @JsonProperty
        public String type;

        @JsonProperty
        public int year;

        @JsonProperty
        public int page;

        @JsonProperty
        public int pageSize;

    }

}
