package uk.gov.legislation.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.time.LocalDate;

public class DateCount {

    @JsonProperty
    public LocalDate date;

    @JsonProperty
    public int count;

    public DateCount(SearchResults.FacetPublishDate facet) {
        date = facet.date;
        count = facet.total;
    }

}
