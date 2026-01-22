package uk.gov.legislation.endpoints.dates;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatesController.class)
class DatesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Search search;

    @Test
    void shouldReturnEmptyList_whenNoPublishedDates() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();
        results.facets.facetPublishDates = Collections.emptyList();

        ArgumentCaptor<Parameters> paramsCaptor = ArgumentCaptor.forClass(Parameters.class);
        when(search.get(any(Parameters.class))).thenReturn(results);

        // Capture the current date before the request to guard against a midnight rollover.
        LocalDate beforeRequest = LocalDate.now();

        mockMvc.perform(get("/dates/published")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[]"));  // Expecting an empty list

        // Second capture after the request so we allow either “yesterday” value.
        LocalDate afterRequest = LocalDate.now();

        verify(search).get(paramsCaptor.capture());

        Parameters capturedParams = paramsCaptor.getValue();
        assertNotNull(capturedParams, "Parameters should not be null");
        assertNotNull(capturedParams.published, "Published date filter should be set");
        assertPublishedDate(beforeRequest, afterRequest, capturedParams.published);
    }

    @Test
    void shouldReturnSingleDateCount_whenOnePublishDate() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();

        SearchResults.FacetPublishDate dateCountFacet = new SearchResults.FacetPublishDate();
        dateCountFacet.date = LocalDate.of(2023, 9, 24);
        dateCountFacet.total = 5;

        results.facets.facetPublishDates = List.of(dateCountFacet);

        ArgumentCaptor<Parameters> paramsCaptor = ArgumentCaptor.forClass(Parameters.class);
        when(search.get(any(Parameters.class))).thenReturn(results);

        LocalDate beforeRequest = LocalDate.now();

        mockMvc.perform(get("/dates/published")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].date").value("2023-09-24"))
            .andExpect(jsonPath("$[0].count").value(5));

        LocalDate afterRequest = LocalDate.now();

        verify(search).get(paramsCaptor.capture());

        Parameters capturedParams = paramsCaptor.getValue();
        assertNotNull(capturedParams, "Parameters should not be null");
        assertNotNull(capturedParams.published, "Published date filter should be set");
        assertPublishedDate(beforeRequest, afterRequest, capturedParams.published);
    }

    @Test
    void shouldReturnMultipleDateCounts_whenMultiplePublishDates() throws Exception {
        SearchResults results = new SearchResults();
        results.facets = new SearchResults.Facets();

        SearchResults.FacetPublishDate dc1 = new SearchResults.FacetPublishDate();
        dc1.date = LocalDate.of(2023, 9, 24);
        dc1.total = 3;

        SearchResults.FacetPublishDate dc2 = new SearchResults.FacetPublishDate();
        dc2.date = LocalDate.of(2023, 9, 23);
        dc2.total = 7;

        results.facets.facetPublishDates = List.of(dc1, dc2);

        ArgumentCaptor<Parameters> paramsCaptor = ArgumentCaptor.forClass(Parameters.class);
        when(search.get(any(Parameters.class))).thenReturn(results);

        LocalDate beforeRequest = LocalDate.now();

        mockMvc.perform(get("/dates/published")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].date").value("2023-09-24"))
            .andExpect(jsonPath("$[0].count").value(3))
            .andExpect(jsonPath("$[1].date").value("2023-09-23"))
            .andExpect(jsonPath("$[1].count").value(7));

        LocalDate afterRequest = LocalDate.now();

        verify(search).get(paramsCaptor.capture());

        Parameters capturedParams = paramsCaptor.getValue();
        assertNotNull(capturedParams, "Parameters should not be null");
        assertNotNull(capturedParams.published, "Published date filter should be set");
        assertPublishedDate(beforeRequest, afterRequest, capturedParams.published);
    }

    private static void assertPublishedDate(LocalDate beforeRequest, LocalDate afterRequest, LocalDate actual) {
        LocalDate expectedBefore = beforeRequest.minusDays(1);
        LocalDate expectedAfter = afterRequest.minusDays(1);
        assertTrue(
            actual.equals(expectedBefore) || actual.equals(expectedAfter),
            () -> "Published date should be yesterday (accepted values: %s or %s) but was %s"
                .formatted(expectedBefore, expectedAfter, actual)
        );
    }

}
