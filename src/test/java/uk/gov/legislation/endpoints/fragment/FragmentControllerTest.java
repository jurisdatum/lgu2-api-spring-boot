package uk.gov.legislation.endpoints.fragment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FragmentController.class)
@AutoConfigureMockMvc
class FragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Transforms transforms;


    @Test
    void shouldReturnFragmentXml_whenValidRequest() throws Exception {

        String expectedXml = "<fragment><section>1</section></fragment>";
        Legislation.Response mockResponse = new Legislation.Response(expectedXml, Optional.empty());

        when(marklogic.getDocumentSection(
            anyString(), anyString(), anyInt(), anyString(), any(Optional.class), any(Optional.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/xml;charset=UTF-8"))
            .andExpect(content().string(expectedXml));
    }

    @Test
    void shouldReturn404_whenPathVariableIsMissing() throws Exception {

        mockMvc.perform(
                get("/fragment/ukla/2020/1") // Missing 'section'
                    .accept(MediaType.APPLICATION_XML)
                    .param("version", "enacted")
                    .header("Accept-Language", "en"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Invalid Year Requested")
    void shouldReturn400_whenYearIsInvalid() throws Exception {
        mockMvc.perform(get("/fragment/ukla/not-a-year/1/section-1")
                .accept("application/xml")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted"))
            .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Default Accept language header")
    void shouldUseDefaultLocale_whenNoAcceptLanguageHeaderProvided() throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                // No Accept-Language header set
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("Content-Language", "en"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("Content-Language", acceptLanguageHeader));
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "application/json",
        "application/akn+xml",
        "text/html",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    })
    @DisplayName("Should return 200 for supported Accept headers")

    void shouldReturn200ForSupportedAcceptHeaders(String acceptHeader) throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection(any(), any(), anyInt(), any(), any(), any()))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(acceptHeader)
                .header("Accept-Language", "en")
                .queryParam("version", "enacted"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unsupported Accept Headers")
    void shouldReturn406ForUnSupportedAcceptHeaders() throws Exception {
        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/abc")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted"))
            .andExpect(status().isNotAcceptable()); // 406
    }
}

