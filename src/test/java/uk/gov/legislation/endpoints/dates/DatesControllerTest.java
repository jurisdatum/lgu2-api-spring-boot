package uk.gov.legislation.endpoints.dates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatesController.class)
class DatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Search search;

    @Test
    void testPublishedWithNoData() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();
        results.facets.facetPublishDates = Collections.emptyList();

        when(search.get(any(Parameters.class))).thenReturn(results);

        mockMvc.perform(get("/dates/published")
                .accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(content().json("[]"));  // Expecting an empty list
    }

    @Test
    void testPublishedWithOneDateCount() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();

        SearchResults.FacetPublishDate dateCountFacet = new SearchResults.FacetPublishDate();
        dateCountFacet.date = LocalDate.of(2023, 9, 24);
        dateCountFacet.total = 5;

        results.facets.facetPublishDates = List.of(dateCountFacet);

        when(search.get(any(Parameters.class))).thenReturn(results);

        mockMvc.perform(get("/dates/published")
                .accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$[0].date").value("2023-09-24"))
            .andExpect(jsonPath("$[0].count").value(5));
    }

    @Test
    void testPublishedWithMultipleDateCounts() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();

        SearchResults.FacetPublishDate dc1 = new SearchResults.FacetPublishDate();
        dc1.date = LocalDate.of(2023, 9, 24);
        dc1.total = 3;

        SearchResults.FacetPublishDate dc2 = new SearchResults.FacetPublishDate();
        dc2.date = LocalDate.of(2023, 9, 23);
        dc2.total = 7;

        results.facets.facetPublishDates = List.of(dc1, dc2);

        when(search.get(any(Parameters.class))).thenReturn(results);

        mockMvc.perform(get("/dates/published")
                .accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$[0].date").value("2023-09-24"))
            .andExpect(jsonPath("$[0].count").value(3))
            .andExpect(jsonPath("$[1].date").value("2023-09-23"))
            .andExpect(jsonPath("$[1].count").value(7));
    }

    @Test
    void testPublishedThrowsException() throws Exception {
        when(search.get(any(Parameters.class))).thenThrow(new IOException("Search service failed"));

        mockMvc.perform(get("/dates/published")
                .accept("application/json"))
            .andExpect(status().isServiceUnavailable());
    }

}