package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class SparqlResults {

    @JsonProperty
    public Results results;

    public static class Results {

        @JsonProperty
        public List<Item> bindings;

    }

    public static class Item {

        @JsonProperty
        public Value item;

        @JsonProperty
        public Value year;

        @JsonProperty
        public Value number;

        public Value title;

        @JsonProperty
        public Value typeLabel;

        @JsonProperty
        public Value reviewDate;

    }

    public static class Value {

        @JsonProperty
        public String value;

    }

    public List<SimpleItem> simplified() {
        return results.bindings.stream().map(item -> {
            SimpleItem simple = new SimpleItem();
            simple.uri = URI.create(item.item.value);
            simple.title = item.title.value;
            simple.year = Integer.parseInt(item.year.value);
            simple.number = item.number.value;
            simple.type = item.typeLabel.value;
            if (item.reviewDate != null)
                simple.review = LocalDate.parse(item.reviewDate.value);
            return simple;
        }).toList();
    }

    public static class SimpleItem {

        @JsonProperty
        public URI uri;

        @JsonProperty
        public String title;

        @JsonProperty
        public int year;

        @JsonProperty
        public String number;

        @JsonProperty
        public String type;

        @JsonProperty
        public LocalDate review;

    }

}
