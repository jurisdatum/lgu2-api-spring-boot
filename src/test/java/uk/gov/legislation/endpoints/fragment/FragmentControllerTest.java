package uk.gov.legislation.endpoints.fragment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FragmentController.class)
class FragmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Transforms transforms;

    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final String expectedXml = "<fragment><section>1</section></fragment>";
    private final Optional<String> version = Optional.of("enacted");
    private final Optional<String> language = Optional.of("en");
    private final String section = "section-1";
    private final String regnalYear = "Eliz1/2020";
    private final String type = "ukla";
    private final String year = "2020";
    private final int number = 1;
    private final Legislation.Response mockResponse = new Legislation.Response(expectedXml, Optional.empty());


    @Test
    void shouldReturnFragmentXml_whenValidRequest() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
            .andExpect(content().string(expectedXml));
        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnFragmentXml_whenValidRequestWithMonarch() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, regnalYear, number, section, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/Eliz1/2020/1/section-1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
            .andExpect(content().string(expectedXml));
        verify(marklogic).getDocumentSectionStream(type, regnalYear, number, section, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnJsonFragmentWhenAcceptsJson() throws Exception {

        when(marklogic.getDocumentSection(
            type, year, number, section, version, language))
            .thenReturn(mockResponse);

        Fragment fragment = new Fragment(null, "rendered fragment");
        when(transforms.clml2fragment(expectedXml)).thenReturn(fragment);

        mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered fragment"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection(type, year, number, section, version, language);
        verify(transforms).clml2fragment(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnJsonFragmentWhenAcceptsJsonWithMonarch() throws Exception {

        when(marklogic.getDocumentSection(
            type, regnalYear, number, section, version, language))
            .thenReturn(mockResponse);

        Fragment fragment = new Fragment(null, "rendered fragment");
        when(transforms.clml2fragment(expectedXml)).thenReturn(fragment);

        mockMvc.perform(get("/fragment/ukla/Eliz1/2020/1/section-1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered fragment"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSection(type, regnalYear, number, section, version, language);
        verify(transforms).clml2fragment(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocx() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);

        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream docxOut = invocation.getArgument(1, OutputStream.class);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            docxOut.write(docx);
            docxOut.flush();
            return null;
        }).when(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.valueOf(DOCX_MIME_TYPE))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf(DOCX_MIME_TYPE)))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verify(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocxWithMonarch() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, regnalYear, number, section, version, language))
            .thenReturn(streamResponse);

        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream docxOut = invocation.getArgument(1, OutputStream.class);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            docxOut.write(docx);
            docxOut.flush();
            return null;
        }).when(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/Eliz1/2020/1/section-1")
                .accept(MediaType.valueOf(DOCX_MIME_TYPE))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf(DOCX_MIME_TYPE)))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, regnalYear, number, section, version, language);
        verify(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlFragmentWhenAcceptsHtml() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream htmlStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            htmlStream.write(renderedHtml.getBytes(StandardCharsets.UTF_8));
            htmlStream.flush();
            return null;
        }).when(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept(MediaType.TEXT_HTML)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verify(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlFragmentWhenAcceptsHtmlWithMonarch() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, regnalYear, number, section, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream htmlStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            htmlStream.write(renderedHtml.getBytes(StandardCharsets.UTF_8));
            htmlStream.flush();
            return null;
        }).when(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/Eliz1/2020/1/section-1")
                .accept(MediaType.TEXT_HTML)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, regnalYear, number, section, version, language);
        verify(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknFragmentWhenAcceptsAkn() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream aknStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            aknStream.write(aknXml.getBytes(StandardCharsets.UTF_8));
            aknStream.flush();
            return null;
        }).when(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/akn+xml")))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verify(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknFragmentWhenAcceptsAknWithMonarch() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, regnalYear, number, section, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream aknStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            aknStream.write(aknXml.getBytes(StandardCharsets.UTF_8));
            aknStream.flush();
            return null;
        }).when(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/Eliz1/2020/1/section-1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("application/akn+xml")))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, regnalYear, number, section, version, language);
        verify(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/fragment/{type}/2020/1/section-1", invalidType)
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
        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.of(redirect));

        when(marklogic.getDocumentSectionStream(type, year, number, section, Optional.empty(), language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .header("Accept-Language", "en")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedXml))
            .andExpect(header().string("X-Document-Type", type))
            .andExpect(header().string("X-Document-Year", year))
            .andExpect(header().string("X-Document-Number", Integer.toString(number)))
            .andExpect(header().string("X-Document-Version", "enacted"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, Optional.empty(), language);
        verifyNoInteractions(transforms);
    }

    @Test
    @DisplayName("Default Accept language header 'en'")
    void shouldUseDefaultLocale_whenNoAcceptLanguageHeaderProvided() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                // No Accept-Language header set
                .queryParam("version", "enacted"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(expectedXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verifyNoInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {

        Optional<String> language = Optional.of(acceptLanguageHeader);

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(expectedXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentSectionStream(type, year, number, section, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/fragment/ukla/2020/1/section-1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(expectedXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, acceptLanguageHeader));

        verify(marklogic).getDocumentSectionStream(type, year, number, section, version, language);
        verifyNoInteractions(transforms);
    }
}
