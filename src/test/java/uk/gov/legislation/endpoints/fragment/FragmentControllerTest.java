package uk.gov.legislation.endpoints.fragment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FragmentController.class)
@AutoConfigureMockMvc(addFilters = false)
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
    @DisplayName("Default Accept language header 'en'")
    void shouldUseDefaultLocale_whenNoAcceptLanguageHeaderProvided() throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of("en");
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                // No Accept-Language header set
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("Content-Language", "en"));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verifyNoInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of(acceptLanguageHeader);
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("Content-Language", acceptLanguageHeader));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verifyNoInteractions(transforms);
    }


    @Test
    void shouldReturnJsonFragmentWhenAcceptsJson() throws Exception {
        String clmlXml = "<some><xml>...</xml></some>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of("en");
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);

        Fragment fragment = new Fragment(null, "rendered fragment");
        when(transforms.clml2fragment(clmlXml)).thenReturn(fragment);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered fragment"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verify(transforms).clml2fragment(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocx() throws Exception {

        String clmlXml = "<some><xml>...</xml></some>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of("en");
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);

        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        when(transforms.clml2docx(clmlXml)).thenReturn(docx);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verify(transforms).clml2docx(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlFragmentWhenAcceptsHtml() throws Exception {
        String clmlXml = "<some><xml>...</xml></some>";
        String renderedHtml = "<html><body>Some content</body></html>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of("en");
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);
        when(transforms.clml2html(clmlXml, true)).thenReturn(renderedHtml);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.TEXT_HTML)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verify(transforms).clml2html(clmlXml, true);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknFragmentWhenAcceptsAkn() throws Exception {
        String clmlXml = "<some><xml>...</xml></some>";
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";
        Optional<String> version = Optional.of("enacted");
        Optional<String> language = Optional.of("en");
        Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

        when(marklogic.getDocumentSection("ukla", "2020", 1, "section-1", version, language))
            .thenReturn(response);
        when(transforms.clml2akn(clmlXml)).thenReturn(aknXml);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/akn+xml;charset=UTF-8"))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection("ukla", "2020", 1, "section-1", version, language);
        verify(transforms).clml2akn(clmlXml);
        verifyNoMoreInteractions(transforms);
    }
}
