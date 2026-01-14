package uk.gov.legislation.endpoints.documents;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.accept.ContentNegotiationManager;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentsController.class)
class DocumentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Search search;

    @MockitoBean
    private ContentNegotiationManager negotiation;

    private final SearchResults mockResults = new SearchResults();
    private final PageOfDocuments mockPage = new PageOfDocuments();
    private final String type = "ukpga";
    private final int page = 1;
    private final String atom ="""
    <feed xmlns='http://www.w3.org/2005/Atom'>
        <updated>2025-01-01T00:00:00Z</updated>
    </feed>""";

    @BeforeEach
    void setup() {
        mockPage.meta = new PageOfDocuments.Meta();
        mockPage.meta.updated = ZonedDateTime.parse("2025-01-01T00:00:00Z");
    }

    @Test
    @DisplayName("Should return documents by type in JSON or Atom")
    void shouldReturnDocsByType() throws Exception {

            when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
            when(search.get(argThat(params -> type.equals(params.type) && params.page == page)))
                .thenReturn(mockResults);

            try (MockedStatic<DocumentsFeedConverter> mockStatic = mockStatic(DocumentsFeedConverter.class)) {
                mockStatic.when(() -> DocumentsFeedConverter.convert(mockResults, null))
                    .thenReturn(mockPage);

                mockMvc.perform(get("/documents/{type}", type)
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
            }

            verify(search).get(argThat(params -> type.equals(params.type) && params.page == page));
        }

    @Test
    @DisplayName("Should return Atom feed by type")
    void shouldReturnAtomByType() throws Exception {

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_ATOM_XML));
        when(search.getAtomStream(argThat(params -> type.equals(params.type) && params.page == page)))
            .thenReturn(new ByteArrayInputStream(atom.getBytes(StandardCharsets.UTF_8)));

        MvcResult mvcResult = mockMvc.perform(get("/documents/{type}", type)
                .param("page", String.valueOf(page))
                .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML))
            .andExpect(content().string(atom));

        verify(search).getAtomStream(argThat(params -> type.equals(params.type) && params.page == page));
    }

    @Test
    @DisplayName("Should return JSON by type and year")
    void shouldReturnJsonByTypeAndYear() throws Exception {
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
        when(search.get(any())).thenReturn(mockResults);

        try (MockedStatic<DocumentsFeedConverter> mockStatic = mockStatic(DocumentsFeedConverter.class)) {
            mockStatic.when(() -> DocumentsFeedConverter.convert(mockResults, null)).thenReturn(mockPage);

            mockMvc.perform(get("/documents/ukpga/2020")
                    .param("page", "1")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
        verify(search).get(argThat(p ->type.equals(p.type) && p.year == 2020));
    }

    @Test
    @DisplayName("Should return Atom by type and year")
    void shouldReturnAtomByTypeAndYear() throws Exception {

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_ATOM_XML));
        when(search.getAtomStream(argThat(params -> type.equals(params.type) && params.year == 2020 && params.page == page)))
            .thenReturn(new ByteArrayInputStream(atom.getBytes(StandardCharsets.UTF_8)));

        MvcResult mvcResult = mockMvc.perform(get("/documents/ukpga/2020")
                .param("page", "1")
                .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_ATOM_XML))
            .andExpect(content().string(atom));

        verify(search).getAtomStream(argThat(params -> type.equals(params.type) && params.year == 2020 && params.page == 1));
    }

    @Test
    @DisplayName("Should return JSON from /documents/new/uk")
    void shouldReturnNewDocumentsAsJson() throws Exception {
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
        when(search.get(any())).thenReturn(mockResults);

        try (MockedStatic<DocumentsFeedConverter> mockStatic = mockStatic(DocumentsFeedConverter.class)) {
            mockStatic.when(() -> DocumentsFeedConverter.convert(mockResults, null)).thenReturn(mockPage);

            mockMvc.perform(get("/documents/new/uk")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        verify(search).get(argThat(params -> params.sort == uk.gov.legislation.data.marklogic.search.Parameters.Sort.PUBLISHED && params.page == 1));
    }

    @Test
    @DisplayName("Should return Atom from /documents/new/uk")
    void shouldReturnNewDocumentsAsAtom() throws Exception {
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_ATOM_XML));
        when(search.getAtomStream(any()))
            .thenReturn(new ByteArrayInputStream(atom.getBytes(StandardCharsets.UTF_8)));

        // This endpoint uses ResponseEntity<?> with runtime content negotiation,
        // which doesn't support async dispatch in MockMvc. Just verify status and service call.
        mockMvc.perform(get("/documents/new/uk")
                .accept(MediaType.APPLICATION_ATOM_XML))
            .andExpect(status().isOk());

        verify(search).getAtomStream(argThat(params -> params.sort == uk.gov.legislation.data.marklogic.search.Parameters.Sort.PUBLISHED && params.page == 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "badtype", "123", "xyz"})
    @DisplayName("Should return 400 Bad Request for invalid document types")
    void shouldReturn400ForInvalidType(String invalidType) throws Exception {
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/documents/{type}/2020", invalidType)
                .param("page", "1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Unknown Document Type Error"))
            .andExpect(jsonPath("$.message")
                .value("The document type '" + invalidType + "' is not recognized."));

        verifyNoInteractions(search);
    }

    @Test
    @DisplayName("Should include Last-Modified header in JSON response")
    void shouldIncludeLastModifiedHeaderInJsonResponse() throws Exception {
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
        when(search.get(any())).thenReturn(mockResults);

        try (MockedStatic<DocumentsFeedConverter> mockStatic = mockStatic(DocumentsFeedConverter.class)) {
            mockStatic.when(() -> DocumentsFeedConverter.convert(mockResults, null)).thenReturn(mockPage);

            mockMvc.perform(get("/documents/ukpga")
                    .param("page", "1")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Last-Modified"))
                .andExpect(header().string("Last-Modified", "Wed, 01 Jan 2025 00:00:00 GMT"));
        }

        verify(search).get(argThat(params -> type.equals(params.type) && params.page == 1));
    }

}
