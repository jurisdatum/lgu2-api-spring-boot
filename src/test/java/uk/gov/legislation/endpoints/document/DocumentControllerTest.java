package uk.gov.legislation.endpoints.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.data.marklogic.impacts.Impacts;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DocumentController.class)
 class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Transforms transforms;

    @MockitoBean
    private Impacts impacts;

    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final Optional<String> version = Optional.of("enacted");
    private final Optional<String> language = Optional.of("en");
    private final String regnalYear = "Eliz1/2020";
    private final String type = "ukla";
    private final String year = "2020";
    private final int number = 1;
    private final String clmlXml = "<document><type>enacted</type></document>";
    private final Legislation.Response response = new Legislation.Response(clmlXml, Optional.empty());

    @Test
    void shouldReturnXml_whenValidRequest() throws Exception {

        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);

        mockMvc.perform(get("/document/ukla/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/xml;charset=UTF-8"))
            .andExpect(content().string(clmlXml));
        verify(marklogic).getDocument(type, year, number, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnXml_whenValidRequestWithMonarch() throws Exception {

        when(marklogic.getDocument(type, regnalYear, number, version, language))
            .thenReturn(response);

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/xml;charset=UTF-8"))
            .andExpect(content().string(clmlXml));
        verify(marklogic).getDocument(type, regnalYear, number, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJson() throws Exception {

        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);

        Document document = new Document(null, "rendered document");
        when(transforms.clml2document(clmlXml)).thenReturn(document);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered document"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, version, language);
        verify(transforms).clml2document(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJsonWithMonarch() throws Exception {

        when(marklogic.getDocument(type, regnalYear, number, version, language))
            .thenReturn(response);

        Document document = new Document(null, "rendered document");
        when(transforms.clml2document(clmlXml)).thenReturn(document);

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered document"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, regnalYear, number, version, language);
        verify(transforms).clml2document(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocx() throws Exception {

        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);

        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        when(transforms.clml2docx(clmlXml)).thenReturn(docx);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept(MediaType.valueOf(DOCX_MIME_TYPE))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf(DOCX_MIME_TYPE)))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, version, language);
        verify(transforms).clml2docx(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocxWithMonarch() throws Exception {

        when(marklogic.getDocument(type, regnalYear, number, version, language))
            .thenReturn(response);

        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        when(transforms.clml2docx(clmlXml)).thenReturn(docx);

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept(MediaType.valueOf(DOCX_MIME_TYPE))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf(DOCX_MIME_TYPE)))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, regnalYear, number, version, language);
        verify(transforms).clml2docx(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlWhenAcceptsHtml() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";
        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);
        when(transforms.clml2html(clmlXml, true)).thenReturn(renderedHtml);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept(MediaType.TEXT_HTML)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, version, language);
        verify(transforms).clml2html(clmlXml, true);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlWhenAcceptsHtmlWithMonarch() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";

        when(marklogic.getDocument(type, regnalYear, number, version, language))
            .thenReturn(response);
        when(transforms.clml2html(clmlXml, true)).thenReturn(renderedHtml);

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept(MediaType.TEXT_HTML)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, regnalYear, number, version, language);
        verify(transforms).clml2html(clmlXml, true);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAkn() throws Exception {

        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);
        when(transforms.clml2akn(clmlXml)).thenReturn(aknXml);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/akn+xml;charset=UTF-8"))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, version, language);
        verify(transforms).clml2akn(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAknWithMonarch() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        when(marklogic.getDocument(type, regnalYear, number, version, language))
            .thenReturn(response);
        when(transforms.clml2akn(clmlXml)).thenReturn(aknXml);

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/akn+xml;charset=UTF-8"))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, regnalYear, number, version, language);
        verify(transforms).clml2akn(clmlXml);
        verifyNoMoreInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/document/{type}/2020/1", invalidType)
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    @DisplayName("Should include redirect headers when MarkLogic returns redirect")
    void shouldIncludeRedirectHeaders_whenMarkLogicReturnsRedirect() throws Exception {
        Legislation.Redirect redirect = new Legislation.Redirect(type, year, number, Optional.of("enacted"));
        Legislation.Response responseWithRedirect = new Legislation.Response(clmlXml, Optional.of(redirect));

        when(marklogic.getDocument(type, year, number, Optional.empty(), language))
            .thenReturn(responseWithRedirect);

        mockMvc.perform(get("/document/ukla/2020/1")
                .header("Accept-Language", "en")
                .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("X-Document-Type", type))
            .andExpect(header().string("X-Document-Year", year))
            .andExpect(header().string("X-Document-Number", Integer.toString(number)))
            .andExpect(header().string("X-Document-Version", "enacted"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, Optional.empty(), language);
        verifyNoInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {
        Optional<String> language = Optional.of(acceptLanguageHeader);

        when(marklogic.getDocument(type, year, number, version, language))
            .thenReturn(response);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, acceptLanguageHeader));

        verify(marklogic).getDocument(type, year, number, version, language);
        verifyNoInteractions(transforms);
    }

    @Test
    @DisplayName("Should default to English when Accept-Language header is missing")
    void shouldDefaultToEnglish_whenAcceptLanguageHeaderIsMissing() throws Exception {
        Optional<String> defaultLanguage = Optional.of("en");

        when(marklogic.getDocument(type, year, number, version, defaultLanguage))
            .thenReturn(response);

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/xml")
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocument(type, year, number, version, defaultLanguage);
        verifyNoInteractions(transforms);
    }

}
