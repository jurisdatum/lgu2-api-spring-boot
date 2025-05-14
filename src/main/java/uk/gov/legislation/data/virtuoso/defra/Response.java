package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Response {

    @JsonProperty
    public Counts counts = new Response.Counts();

    @JsonProperty
    public List<SparqlResults.SimpleItem> results;

    public static class Counts {

        @JsonProperty
        public List<TypeFacets.TypeCount> byType;

        @JsonProperty
        public Map<Integer, Integer> byYear;

        @JsonProperty
        public List<ChapterFacets.ChapterCount> byChapter;

    }

}
