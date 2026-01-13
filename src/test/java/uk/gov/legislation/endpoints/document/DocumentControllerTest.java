package uk.gov.legislation.endpoints.document;

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
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.data.marklogic.impacts.Impacts;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.exceptions.TransformationException;
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

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().string(clmlXml));
        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnXml_whenValidRequestWithMonarch() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, regnalYear, number, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().string(clmlXml));
        verify(marklogic).getDocumentStream(type, regnalYear, number, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJson() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);

        Document document = new Document(null, "rendered document");
        when(transforms.clml2document(any(InputStream.class))).then(invocation -> {
            invocation.getArgument(0, InputStream.class);
            return document;
        });

        mockMvc.perform(get("/document/ukla/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered document"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verify(transforms).clml2document(any(InputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJsonWithMonarch() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, regnalYear, number, version, language))
            .thenReturn(streamResponse);

        Document document = new Document(null, "rendered document");
        when(transforms.clml2document(any(InputStream.class))).then(invocation -> {
            invocation.getArgument(0, InputStream.class);
            return document;
        });

        mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.html").value("rendered document"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, regnalYear, number, version, language);
        verify(transforms).clml2document(any(InputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocx() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
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

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
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

        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verify(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocxWithMonarch() throws Exception {

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, regnalYear, number, version, language))
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

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
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

        verify(marklogic).getDocumentStream(type, regnalYear, number, version, language);
        verify(transforms).clml2docx(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlWhenAcceptsHtml() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream htmlStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            htmlStream.write(renderedHtml.getBytes(StandardCharsets.UTF_8));
            htmlStream.flush();
            return null;
        }).when(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
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

        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verify(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnHtmlWhenAcceptsHtmlWithMonarch() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, regnalYear, number, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream htmlStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            htmlStream.write(renderedHtml.getBytes(StandardCharsets.UTF_8));
            htmlStream.flush();
            return null;
        }).when(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
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

        verify(marklogic).getDocumentStream(type, regnalYear, number, version, language);
        verify(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAkn() throws Exception {

        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream aknStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            aknStream.write(aknXml.getBytes(StandardCharsets.UTF_8));
            aknStream.flush();
            return null;
        }).when(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.parseMediaType("application/akn+xml")))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verify(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAknWithMonarch() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, regnalYear, number, version, language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream aknStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            aknStream.write(aknXml.getBytes(StandardCharsets.UTF_8));
            aknStream.flush();
            return null;
        }).when(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/Eliz1/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.parseMediaType("application/akn+xml")))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, regnalYear, number, version, language);
        verify(transforms).clml2akn(any(InputStream.class), any(OutputStream.class));
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

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.of(redirect));
        when(marklogic.getDocumentStream(type, year, number, Optional.empty(), language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .header("Accept-Language", "en")
                .accept("application/xml"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().string(clmlXml))
            .andExpect(header().string("X-Document-Type", type))
            .andExpect(header().string("X-Document-Year", year))
            .andExpect(header().string("X-Document-Number", Integer.toString(number)))
            .andExpect(header().string("X-Document-Version", "enacted"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, year, number, Optional.empty(), language);
        verifyNoInteractions(transforms);
    }

    @Test
    @DisplayName("Should include redirect headers for HTML streaming")
    void shouldIncludeRedirectHeaders_whenHtmlStreaming() throws Exception {
        String renderedHtml = "<html><body>Some content</body></html>";
        Legislation.Redirect redirect = new Legislation.Redirect(type, year, number, Optional.of("enacted"));
        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.of(redirect));
        when(marklogic.getDocumentStream(type, year, number, Optional.empty(), language))
            .thenReturn(streamResponse);
        doAnswer(invocation -> {
            InputStream clmlIn = invocation.getArgument(0, InputStream.class);
            OutputStream htmlStream = invocation.getArgument(1);
            clmlIn.transferTo(OutputStream.nullOutputStream());
            htmlStream.write(renderedHtml.getBytes(StandardCharsets.UTF_8));
            htmlStream.flush();
            return null;
        }).when(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .accept(MediaType.TEXT_HTML)
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(renderedHtml))
            .andExpect(header().string("X-Document-Type", type))
            .andExpect(header().string("X-Document-Year", year))
            .andExpect(header().string("X-Document-Number", Integer.toString(number)))
            .andExpect(header().string("X-Document-Version", "enacted"))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, year, number, Optional.empty(), language);
        verify(transforms).clml2htmlStandalone(any(InputStream.class), any(OutputStream.class));
        verifyNoMoreInteractions(transforms);
    }

    @Test
    @DisplayName("AkN transform failure returns 500 with error JSON")
    void shouldReturn500WhenAknTransformFails() throws Exception {
        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);
        doThrow(new TransformationException("boom", null)).when(transforms)
            .clml2akn(any(InputStream.class), any(OutputStream.class));

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isInternalServerError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {
        Optional<String> language = Optional.of(acceptLanguageHeader);

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, language))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, acceptLanguageHeader));

        verify(marklogic).getDocumentStream(type, year, number, version, language);
        verifyNoInteractions(transforms);
    }

    @Test
    @DisplayName("Should default to English when Accept-Language header is missing")
    void shouldDefaultToEnglish_whenAcceptLanguageHeaderIsMissing() throws Exception {
        Optional<String> defaultLanguage = Optional.of("en");

        Legislation.StreamResponse streamResponse = new Legislation.StreamResponse(
            new ByteArrayInputStream(clmlXml.getBytes(StandardCharsets.UTF_8)),
            Optional.empty());
        when(marklogic.getDocumentStream(type, year, number, version, defaultLanguage))
            .thenReturn(streamResponse);

        MvcResult mvcResult = mockMvc.perform(get("/document/ukla/2020/1")
                .accept("application/xml")
                .queryParam("version", "enacted"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_XML))
            .andExpect(content().string(clmlXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getDocumentStream(type, year, number, version, defaultLanguage);
        verifyNoInteractions(transforms);
    }

}
