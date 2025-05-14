package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
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
            simple.number = Integer.parseInt(item.number.value);
            simple.type = item.typeLabel.value;
            return simple;
        }).toList();
    }

    public static class SimpleItem {

        @JsonProperty
        public URI uri;

        public String title;

        @JsonProperty
        public int year;

        @JsonProperty
        public int number;

        @JsonProperty
        public String type;

    }

}
