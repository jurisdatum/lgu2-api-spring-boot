package uk.gov.legislation.endpoints.ld.regnal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.accept.ContentNegotiationManager;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalYearLD;
import uk.gov.legislation.data.virtuoso.queries.RegnalYearQuery;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegnalYearController.class)
class RegnalYearControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegnalYearQuery query;

    @MockitoBean
    private ContentNegotiationManager negotiation;

    private final String reign = "Eliz1";
    private final Integer regnalYear = 1;
    private final String rawResult = "<raw>linked-data</raw>";

    static Stream<Arguments> supportedVirtuosoFormats() {
        return Stream.of(
            Arguments.of("application/ld+json"),
            Arguments.of("application/rdf+xml"),
            Arguments.of("application/rdf+json"),
            Arguments.of("application/sparql-results+json"),
            Arguments.of("application/sparql-results+xml"),
            Arguments.of("text/csv"),
            Arguments.of("text/plain"),
            Arguments.of("text/turtle")
        );
    }

    @ParameterizedTest
    @MethodSource("supportedVirtuosoFormats")
    @DisplayName("Should return raw data for all supported Virtuoso formats")
    void shouldReturnRawDataForSupportedFormats(String acceptHeader) throws Exception {
        MediaType requestedType = MediaType.valueOf(acceptHeader);

        ArgumentCaptor<String> reignCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> mediaCaptor = ArgumentCaptor.forClass(String.class);

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(requestedType));
        when(query.fetchRawData(any(), any(), any())).thenReturn(rawResult);

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                .accept(acceptHeader))
            .andExpect(status().isOk())
            .andExpect(content().contentType(acceptHeader))
            .andExpect(content().string(rawResult));

        verify(query).fetchRawData(reignCaptor.capture(), yearCaptor.capture(), mediaCaptor.capture());
        assertEquals(reign, reignCaptor.getValue(), "Reign parameter should match");
        assertEquals(regnalYear, yearCaptor.getValue(), "Regnal year parameter should match");
        assertEquals(requestedType.toString(), mediaCaptor.getValue(), "Media type should match requested format");
        verifyNoMoreInteractions(query);
    }

    @DisplayName("Should fallback or reject on unsupported media type")
    @Test
    void shouldHandleUnsupportedMediaType() throws Exception {
        MediaType requestedType = MediaType.valueOf("application/x-custom");

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(requestedType));

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                    .accept(requestedType))
                .andExpect(status().isNotAcceptable());

        // Verify query methods are NOT called for unsupported types
        verifyNoInteractions(query);
    }

    @Test
    @DisplayName("Should return converted mapped data for JSON format")
    void shouldReturnMappedDataForJson() throws Exception {
        // Create test data for the mapped path
        RegnalYearLD regnalYearLD = new RegnalYearLD();
        regnalYearLD.id = URI.create("http://www.legislation.gov.uk/id/regnal-year/Eliz1/1");
        regnalYearLD.label = "1st year of reign of Queen Elizabeth I";
        regnalYearLD.yearOfReign = 1;
        regnalYearLD.reign = URI.create("http://www.legislation.gov.uk/id/reign/Eliz1");
        regnalYearLD.startDate = URI.create("http://www.legislation.gov.uk/def/date/1558-11-17");
        regnalYearLD.endDate = URI.create("http://www.legislation.gov.uk/def/date/1559-11-16");

        ArgumentCaptor<String> reignCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
        when(query.fetchMappedData(any(), any())).thenReturn(Optional.of(regnalYearLD));

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.uri").value("http://www.legislation.gov.uk/id/regnal-year/Eliz1/1"))
            .andExpect(jsonPath("$.label").value("1st year of reign of Queen Elizabeth I"))
            .andExpect(jsonPath("$.yearOfReign").value(1))
            .andExpect(jsonPath("$.reign").value("Eliz1"))
            .andExpect(jsonPath("$.startDate").value("1558-11-17"))
            .andExpect(jsonPath("$.endDate").value("1559-11-16"));

        verify(query).fetchMappedData(reignCaptor.capture(), yearCaptor.capture());
        assertEquals(reign, reignCaptor.getValue(), "Reign parameter should match");
        assertEquals(regnalYear, yearCaptor.getValue(), "Regnal year parameter should match");
        verifyNoMoreInteractions(query);
    }

    @Test
    @DisplayName("Should return 404 when mapped data not found")
    void shouldReturn404WhenMappedDataNotFound() throws Exception {
        ArgumentCaptor<String> reignCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(MediaType.APPLICATION_JSON));
        when(query.fetchMappedData(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(query).fetchMappedData(reignCaptor.capture(), yearCaptor.capture());
        assertEquals(reign, reignCaptor.getValue(), "Reign parameter should match");
        assertEquals(regnalYear, yearCaptor.getValue(), "Regnal year parameter should match");
        verifyNoMoreInteractions(query);
    }

}
