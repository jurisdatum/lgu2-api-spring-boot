package uk.gov.legislation.endpoints.pdf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.endpoints.pdf.controller.PdfApiController;
import uk.gov.legislation.endpoints.pdf.service.PdfService;
import uk.gov.legislation.exceptions.GlobalExceptionHandler;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.exceptions.NoDocumentException;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PdfApiControllerTest {

    private MockMvc mockMvc;

    private StubPdfService pdfService;

    @BeforeEach
    void setUp() {
        pdfService = new StubPdfService();
        mockMvc = MockMvcBuilders.standaloneSetup(new PdfApiController(pdfService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    @DisplayName("Should return 404 when the PDF lookup raises NoDocumentException")
    void shouldReturn404WhenPdfLookupThrowsNoDocumentException() throws Exception {
        Error error = new Error();
        error.statusCode = 404;
        error.message = "Table of contents not found";
        pdfService.failure = new NoDocumentException(error);

        mockMvc.perform(get("/pdf/ukpga/2024/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Document Not Found"))
            .andExpect(jsonPath("$.message").value("Table of contents not found"));
    }

    @Test
    @DisplayName("Should return 500 when the PDF lookup raises MarkLogicRequestException")
    void shouldReturn500WhenPdfLookupThrowsMarkLogicRequestException() throws Exception {
        pdfService.failure = new MarkLogicRequestException("Error parsing MarkLogic error response");

        mockMvc.perform(get("/pdf/ukpga/2024/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("MarkLogic Error"))
            .andExpect(jsonPath("$.message").value("Error parsing MarkLogic error response"));
    }

    private static final class StubPdfService extends PdfService {

        private RuntimeException failure;

        private StubPdfService() {
            super(mock(Legislation.class), null);
        }

        @Override
        public Optional<String> fetchPdfUrl(String type, String yearOrRegnal, int number, String version) {
            if (failure != null)
                throw failure;
            return Optional.empty();
        }

    }

}
