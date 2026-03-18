package uk.gov.legislation.endpoints.effects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.data.marklogic.changes.Parameters;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;
import uk.gov.legislation.transform.simple.effects.Page;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EffectsController.class)
class EffectsControllerTest {

    private static final String VALID_TARGET_TYPE = "ukpga";
    private static final int VALID_TARGET_YEAR = 2021;
    private static final String MOCK_ATOM_FEED = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <entry>
                <title>Mock Entry</title>
                <id>mock-id</id>
            </entry>
        </feed>
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Changes db;

    @MockitoBean
    private EffectsSimplifier simplifier;

    @Test
    @DisplayName("Should return Atom XML for valid parameters")
    void shouldReturnAtomXmlForEffects() throws Exception {
        stubFetchForValidEffectsRequest();

        mockMvc.perform(validEffectsRequest(MediaType.APPLICATION_ATOM_XML)
                .param("sourceTitle", "B"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML_VALUE))
            .andExpect(content().string(containsString("<feed")));
    }

    @Test
    @DisplayName("Should return JSON for valid parameters")
    void shouldReturnJsonForEffects() throws Exception {
        stubFetchForValidEffectsRequest();

        Page mockPage = mock(Page.class);
        when(simplifier.parse(MOCK_ATOM_FEED)).thenReturn(mockPage);

        PageOfEffects mockPageOfEffects = mock(PageOfEffects.class);

        try (MockedStatic<EffectsFeedConverter> effectsFeedConverter = mockStatic(EffectsFeedConverter.class)) {
            effectsFeedConverter.when(() -> EffectsFeedConverter.convert(mockPage)).thenReturn(mockPageOfEffects);

            mockMvc.perform(validEffectsRequest(MediaType.APPLICATION_JSON)
                    .param("sourceTitle", "Banana"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
        }
    }

    @Test
    @DisplayName("Should bind sort query parameter")
    void shouldBindSortQueryParameter() throws Exception {
        when(db.fetch(any())).thenReturn(MOCK_ATOM_FEED);

        mockMvc.perform(get("/effects")
                .accept(MediaType.APPLICATION_ATOM_XML_VALUE)
                .param("sort", "affecting-title"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML_VALUE));

        ArgumentCaptor<Parameters> captor = ArgumentCaptor.forClass(Parameters.class);
        verify(db).fetch(captor.capture());
        assertEquals("affecting-title", captor.getValue().sort,
            "sort query parameter should flow through to the changes request");
        verifyNoInteractions(simplifier);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/effects")
                .param("targetType", invalidType)
                .param("targetYear", String.valueOf(VALID_TARGET_YEAR))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
                .value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(db);
        verifyNoInteractions(simplifier);
    }

    private void stubFetchForValidEffectsRequest() {
        when(db.fetch(argThat(params ->
            VALID_TARGET_TYPE.equals(params.affectedType) &&
                Integer.valueOf(VALID_TARGET_YEAR).equals(params.affectedYear)
        ))).thenReturn(MOCK_ATOM_FEED);
    }

    private MockHttpServletRequestBuilder validEffectsRequest(MediaType accept) {
        return get("/effects")
            .accept(accept)
            .param("targetType", VALID_TARGET_TYPE)
            .param("targetYear", String.valueOf(VALID_TARGET_YEAR))
            .param("targetTitle", "Apple")
            .param("sourceType", "ukla")
            .param("sourceYear", "2012")
            .param("sourceNumber", "3")
            .param("page", "2");
    }
}
