package uk.gov.legislation.endpoints.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.data.marklogic.impacts.Impacts;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.response.ExpectedWelshResponseDataCy;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Transforms;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.transform.simple.UnappliedEffectsHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import({ Transforms.class, Clml2Akn.class, Akn2Html.class, Simplify.class, Clml2Docx.class})
class WelshDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private Transforms transforms;

    @MockitoBean
    private Legislation mock;

    @BeforeEach
    void setUp() throws Exception {
        // Stub the streaming InputStream-based method to use the old String-based method
        // This keeps the generated IDs stable for test assertions
        doAnswer(invocation -> {
            InputStream clmlStream = invocation.getArgument(0);
            String clmlString = new String(clmlStream.readAllBytes(), StandardCharsets.UTF_8);
            return transforms.clml2document(clmlString);
        }).when(transforms).clml2document(any(InputStream.class));
    }

    @MockitoBean
    private Impacts impacts;

    @Test
    void test() throws Exception {

        String clml = UnappliedEffectsHelper.read("/document_wsi_2024_1002_cy.xml");
        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(mock.getDocumentStream("wsi", "2024", 1002, Optional.of("made"), Optional.of("cy")))
            .thenReturn(streamResponse);

        mockMvc.perform(get("/document/wsi/2024/1002")
            .param("version", "made")
            .header("Accept-Language", "cy"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(ExpectedWelshResponseDataCy.DOCUMENT_WELSH_RESPONSE_CY_JSON));
    }

}
