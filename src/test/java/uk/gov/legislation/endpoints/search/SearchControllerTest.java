package uk.gov.legislation.endpoints.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.search.Search;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Search search;

    private final String type = "ukpga";
    private final String year = "2021";
    private final String startYear = "2025";
    private final String endYear = "2020";

    @Test
    @DisplayName("Should return Atom XML for valid parameters")
    void shouldReturnAtomXmlForValidSearch() throws Exception {

        String mockAtomFeed = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <title>Test Feed</title>
            <entry>
                <title>Document 1</title>
                <id>doc-1</id>
                <updated>2025-01-01T00:00:00Z</updated>
            </entry>
        </feed>
        """;

        when(search.getAtom(argThat(params ->
            "ukpga".equals(params.type) &&
                Integer.valueOf(2021).equals(params.year)
        ))).thenReturn(mockAtomFeed);

        mockMvc.perform(get("/search")
                .accept(MediaType.APPLICATION_ATOM_XML_VALUE)
                .param("type", type)
                .param("year", year))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML_VALUE))
            .andExpect(content().string(containsString("<feed")));
    }

    @Test
    @DisplayName("Should return 400 when 'year' is combined with 'startYear'")
    void shouldReturn400ForYearAndStartYearConflict() throws Exception {

        mockMvc.perform(get("/search")
                .param("type", type)
                .param("year", year)
                .param("startYear", startYear)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("`year` cannot be combined with `startYear` or `endYear`"));

        verifyNoInteractions(search);
    }

    @Test
    @DisplayName("Should return 400 when 'year' is combined with 'endYear'")
    void shouldReturn400ForYearAndEndYearConflict() throws Exception {
        mockMvc.perform(get("/search")
                .param("type", type)
                .param("year", year)
                .param("endYear", endYear)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("`year` cannot be combined with `startYear` or `endYear`"));

        verifyNoInteractions(search);
    }

    @Test
    @DisplayName("Should return 400 when 'startYear' is greater than 'endYear'")
    void shouldReturn400ForInvalidYearRange() throws Exception {
        mockMvc.perform(get("/search")
                .param("type", type)
                .param("startYear", startYear)
                .param("endYear", endYear)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("`startYear` must be ≤ `endYear`"));

        verifyNoInteractions(search);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/search")
                .param("type", invalidType)
                .param("year", year)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
                .value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(search);
    }
}



