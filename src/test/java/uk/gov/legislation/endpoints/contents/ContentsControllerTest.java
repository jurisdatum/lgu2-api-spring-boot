package uk.gov.legislation.endpoints.contents;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.data.marklogic.legislation.Legislation;
import uk.gov.legislation.transform.Transforms;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ContentsController.class)
class ContentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Legislation marklogic;

    @MockitoBean
    private Transforms transforms;

    private final Optional<String> version = Optional.of("enacted");
    private final Optional<String> language = Optional.of("en");
    private final String regnalYear = "Eliz1/2020";
    private final String type = "ukla";
    private final String year = "2020";
    private final int number = 1;
    private final String expectedXml = "<contents>something</contents>";
    private final Legislation.Response mockResponse = new Legislation.Response(expectedXml, Optional.empty());

    @Test
    void testValidRequestReturnsXml() throws Exception {

        when(marklogic.getTableOfContents(
            type, year, number, version, language))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/contents/ukla/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
            .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/xml;charset=UTF-8"))
            .andExpect(content().string(expectedXml));
    }

    @Test
    void testValidRequestReturnsXmlWithMonarch() throws Exception {
        when(marklogic.getTableOfContents(
            type, regnalYear, number, version, language))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/contents/ukla/Eliz1/2020/1")
                .header("Accept-Language", "en")
                .queryParam("version", "enacted")
                .accept("application/xml"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/xml;charset=UTF-8"))
            .andExpect(content().string(expectedXml));
        verify(marklogic).getTableOfContents(type, regnalYear, number, version, language);
        verifyNoMoreInteractions(marklogic);
        verifyNoInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJson() throws Exception {
        when(marklogic.getTableOfContents(
            type, year, number, version, language))
            .thenReturn(mockResponse);
        TableOfContents document = new TableOfContents();
        document.meta = mock(DocumentMetadata.class);
        document.contents = mock(TableOfContents.Contents.class);

        when(transforms.clml2toc(expectedXml)).thenReturn(document);
        mockMvc.perform(get("/contents/ukla/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, year, number, version, language);
        verify(transforms).clml2toc(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnJsonWhenAcceptsJsonWithMonarch() throws Exception {
        when(marklogic.getTableOfContents(
            type, regnalYear, number, version, language))
            .thenReturn(mockResponse);
        TableOfContents document = new TableOfContents();
        document.meta = mock(DocumentMetadata.class);
        document.contents = mock(TableOfContents.Contents.class);

        when(transforms.clml2toc(expectedXml)).thenReturn(document);
        mockMvc.perform(get("/contents/ukla/Eliz1/2020/1")
                .accept(MediaType.APPLICATION_JSON)
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, regnalYear, number, version, language);
        verify(transforms).clml2toc(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAkn() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        when(marklogic.getTableOfContents(
            type, year, number, version, language))
            .thenReturn(mockResponse);
        when(transforms.clml2akn(expectedXml)).thenReturn(aknXml);

        mockMvc.perform(get("/contents/ukla/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/akn+xml;charset=UTF-8"))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, year, number, version, language);
        verify(transforms).clml2akn(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnAknWhenAcceptsAknWithMonarch() throws Exception {
        String aknXml = "<akn:doc xmlns:akn='http://www.akomantoso.org/2.0'>...</akn:doc>";

        when(marklogic.getTableOfContents(
            type, regnalYear, number, version, language))
            .thenReturn(mockResponse);
        when(transforms.clml2akn(expectedXml)).thenReturn(aknXml);

        mockMvc.perform(get("/contents/ukla/Eliz1/2020/1")
                .accept("application/akn+xml")
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/akn+xml;charset=UTF-8"))
            .andExpect(content().string(aknXml))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, regnalYear, number, version, language);
        verify(transforms).clml2akn(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocx() throws Exception {

        when(marklogic.getTableOfContents(
            type, year, number, version, language))
            .thenReturn(mockResponse);
        byte[] docx = new byte[] { 0x01, 0x02, 0x03 };
        when(transforms.clml2docx(expectedXml)).thenReturn(docx);

        mockMvc.perform(get("/contents/ukla/2020/1")
                .accept(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, year, number, version, language);
        verify(transforms).clml2docx(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @Test
    void shouldReturnDocxWhenAcceptsDocxWithMonarch() throws Exception {

        when(marklogic.getTableOfContents(
            type, regnalYear, number, version, language))
            .thenReturn(mockResponse);

        byte[] docx = new byte[]{0x01, 0x02, 0x03};
        when(transforms.clml2docx(expectedXml)).thenReturn(docx);

        mockMvc.perform(get("/contents/ukla/Eliz1/2020/1")
                .accept(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .param("version", "enacted")
                .header("Accept-Language", "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")))
            .andExpect(content().bytes(docx))
            .andExpect(header().string(HttpHeaders.CONTENT_LANGUAGE, "en"));

        verify(marklogic).getTableOfContents(type, regnalYear, number, version, language);
        verify(transforms).clml2docx(expectedXml);
        verifyNoMoreInteractions(transforms);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {

        mockMvc.perform(get("/contents/{type}/2020/1", invalidType)
                .header("Accept-Language", "en")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(marklogic);
        verifyNoInteractions(transforms);

    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "cy"})
    @DisplayName("Accept language header")
    void acceptLanguageHeader(String acceptLanguageHeader) throws Exception {

        Optional<String> language = Optional.of(acceptLanguageHeader);
        when(marklogic.getTableOfContents(
            type, year, number, version, language))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/contents/ukla/2020/1")
                .accept("application/xml")
                .header("Accept-Language", acceptLanguageHeader)
                .queryParam("version", "enacted"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/xml"))
            .andExpect(content().string(expectedXml))
            .andExpect(header().string("Content-Language", acceptLanguageHeader));

        verify(marklogic).getTableOfContents(type, year, number, version, language);
        verifyNoInteractions(transforms);
    }

}
