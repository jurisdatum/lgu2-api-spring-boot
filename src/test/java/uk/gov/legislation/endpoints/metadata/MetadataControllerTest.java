package uk.gov.legislation.endpoints.metadata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.Application;
import uk.gov.legislation.data.marklogic.legislation.Legislation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class MetadataControllerTest {

    private static final String TYPE = "asp";
    private static final String YEAR = "2025";
    private static final int NUMBER = 11;
    private static final String LANGUAGE = "en";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @Test
    void shouldReturnXmlMetadataWithContentLanguage() throws Exception {
        String clml = readClasspath("/asp_2025_11/asp-2025-11-metadata.xml");
        Legislation.Response response = new Legislation.Response(clml, Optional.empty());
        when(marklogic.getMetadata(TYPE, YEAR, NUMBER, Optional.of(LANGUAGE))).thenReturn(response);

        mockMvc.perform(get("/metadata/{type}/{year}/{number}", TYPE, YEAR, NUMBER)
                .accept(MediaType.APPLICATION_XML)
                .header("Accept-Language", LANGUAGE))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
            .andExpect(content().xml(clml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, LANGUAGE));

        verify(marklogic).getMetadata(TYPE, YEAR, NUMBER, Optional.of(LANGUAGE));
        verifyNoMoreInteractions(marklogic);
        reset(marklogic);
    }

    @Test
    void shouldReturnJsonMetadataMatchingFixture() throws Exception {
        String clml = readClasspath("/asp_2025_11/asp-2025-11-metadata.xml");
        String expectedJson = readClasspath("/asp_2025_11/asp-2025-11-metadata.json");
        Legislation.Response response = new Legislation.Response(clml, Optional.empty());
        when(marklogic.getMetadata(TYPE, YEAR, NUMBER, Optional.of(LANGUAGE))).thenReturn(response);

        mockMvc.perform(get("/metadata/{type}/{year}/{number}", TYPE, YEAR, NUMBER)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", LANGUAGE))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, LANGUAGE));

        verify(marklogic).getMetadata(TYPE, YEAR, NUMBER, Optional.of(LANGUAGE));
        verifyNoMoreInteractions(marklogic);
        reset(marklogic);
    }

    private static String readClasspath(String path) throws IOException {
        try (InputStream stream = MetadataControllerTest.class.getResourceAsStream(path)) {
            assertThat(stream).isNotNull();
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
