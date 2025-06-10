package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Response {

    @JsonProperty
    public Parameters query;

    @JsonProperty
    public Counts counts = new Counts();

    @JsonProperty
    public List<SparqlResults.SimpleItem> results;

    public static class Counts {

        @JsonProperty
        public Integer total;

        @JsonProperty
        public List<BooleanFacets.Count> byInForce;

        @JsonProperty
        public List<LabeledFacets.Count> byType;

        @JsonProperty
        public List<YearFacets.Count> byYear;

        @JsonProperty
        public List<LabeledFacets.Count> byChapter;

        @JsonProperty
        public List<LabeledFacets.Count> byExtent;

        @JsonProperty
        public List<LabeledFacets.Count> bySource;

        @JsonProperty
        public List<LabeledFacets.Count> byRegulator;

        @JsonProperty
        public List<LabeledFacets.Count> bySubject;

        @JsonProperty
        public List<YearFacets.Count> byReviewDate;

    }

}
