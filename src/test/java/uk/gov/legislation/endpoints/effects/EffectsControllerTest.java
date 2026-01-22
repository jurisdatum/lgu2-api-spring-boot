package uk.gov.legislation.endpoints.effects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;
import uk.gov.legislation.transform.simple.effects.Page;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EffectsController.class)
 class EffectsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Changes db;

    @MockitoBean
    private EffectsSimplifier simplifier;

    private final String targetType = "ukpga";
    private final Integer targetYear = 2021;


    @Test
    @DisplayName("Should return Atom XML for valid parameters")
    void shouldReturnAtomXmlForEffects() throws Exception {

        // expected mock Atom Feed response
        String mockAtomFeed = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <entry>
                <title>Mock Entry</title>
                <id>mock-id</id>
            </entry>
        </feed>
        """;

        // Setup the mock to return the Atom feed for matching parameters
        when(db.fetch(argThat(params ->
            "ukpga".equals(params.affectedType) &&
                Integer.valueOf(2021).equals(params.affectedYear)
        ))).thenReturn(mockAtomFeed);

        mockMvc.perform(get("/effects")
                .accept(MediaType.APPLICATION_ATOM_XML_VALUE)
                .param("targetType", targetType)
                .param("targetYear", String.valueOf(targetYear))
                .param("targetTitle", "Apple")
                .param("sourceType", "ukla")
                .param("sourceYear", "2012")
                .param("sourceNumber", "3")
                .param("sourceTitle", "B")
                .param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML_VALUE))
            .andExpect(content().string(containsString("<feed")));
    }

    @Test
    @DisplayName("Should return JSON for valid parameters")
    void shouldReturnJsonForEffects() throws Exception {

        // Mock the Atom XML response for the atom() method
        String mockAtomFeed = """
        <feed xmlns="http://www.w3.org/2005/Atom">
            <entry>
                <title>Mock Entry</title>
                <id>mock-id</id>
            </entry>
        </feed>
        """;

        when(db.fetch(argThat(params ->
            "ukpga".equals(params.affectedType) &&
                Integer.valueOf(2021).equals(params.affectedYear)
        ))).thenReturn(mockAtomFeed);

        // Mock the simplifier.parse() method to return a mock Page object
        Page mockPage = mock(Page.class);
        when(simplifier.parse(mockAtomFeed)).thenReturn(mockPage);

        // Mock the EffectsFeedConverter.convert() to return a mock PageOfEffects object
        PageOfEffects mockPageOfEffects = mock(PageOfEffects.class);

        try (MockedStatic<EffectsFeedConverter> effectsFeedConverter = mockStatic(EffectsFeedConverter.class)) {
            effectsFeedConverter.when(() -> EffectsFeedConverter.convert(mockPage)).thenReturn(mockPageOfEffects);

            mockMvc.perform(get("/effects")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .param("targetType", targetType)
                    .param("targetYear", String.valueOf(targetYear))
                    .param("targetTitle", "Apple")
                    .param("sourceType", "ukla")
                    .param("sourceYear", "2012")
                    .param("sourceNumber", "3")
                    .param("sourceTitle", "Banana")
                    .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/effects")
                .param("targetType", invalidType)
                .param("targetYear", String.valueOf(targetYear))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error")
                .value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(db);
        verifyNoInteractions(simplifier);
    }
}
