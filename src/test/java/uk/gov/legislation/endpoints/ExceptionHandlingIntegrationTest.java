package uk.gov.legislation.endpoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.exceptions.NoDocumentException;
import uk.gov.legislation.transform.Transforms;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for exception handling across the application.
 * Tests the integration between controllers and GlobalExceptionHandler.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Search search;

    @MockitoBean
    private Transforms transforms;

    @Test
    @DisplayName("Should return 503 when DatesController search service fails with IOException")
    void shouldReturn503_whenSearchServiceFailsWithIOException() throws Exception {
        when(search.get(any(Parameters.class))).thenThrow(new IOException("Search service failed"));

        mockMvc.perform(get("/dates/published")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 404 when DocumentController cannot find document")
    void shouldReturn404_whenDocumentNotFound() throws Exception {
        Error error = new Error();
        error.statusCode = 404;
        error.message = "Document not found";

        when(marklogic.getDocumentStream(anyString(), anyString(), anyInt(), any(), any()))
            .thenThrow(new NoDocumentException(error));

        mockMvc.perform(get("/document/ukpga/2020/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 400 when document type is invalid")
    void shouldReturn400_whenInvalidDocumentType() throws Exception {
        mockMvc.perform(get("/document/invalid-type/2020/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message").value("The document type 'invalid-type' is not recognized."));
    }

    @Test
    @DisplayName("Should return 404 when FragmentController cannot find document section")
    void shouldReturn404_whenFragmentNotFound() throws Exception {
        Error error = new Error();
        error.statusCode = 404;
        error.message = "Document section not found";

        when(marklogic.getDocumentSection(anyString(), anyString(), anyInt(), anyString(), any(), any()))
            .thenThrow(new NoDocumentException(error));

        mockMvc.perform(get("/fragment/ukpga/2020/1/section-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 400 when fragment type is invalid")
    void shouldReturn400_whenInvalidFragmentType() throws Exception {
        mockMvc.perform(get("/fragment/invalid-type/2020/1/section-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message").value("The document type 'invalid-type' is not recognized."));
    }

    @Test
    @DisplayName("Should return 404 when ContentsController cannot find table of contents")
    void shouldReturn404_whenContentsNotFound() throws Exception {
        Error error = new Error();
        error.statusCode = 404;
        error.message = "Table of contents not found";

        when(marklogic.getTableOfContents(anyString(), anyString(), anyInt(), any(), any()))
            .thenThrow(new NoDocumentException(error));

        mockMvc.perform(get("/contents/ukpga/2020/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 400 when contents type is invalid")
    void shouldReturn400_whenInvalidContentsType() throws Exception {
        mockMvc.perform(get("/contents/invalid-type/2020/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message").value("The document type 'invalid-type' is not recognized."));
    }

}
