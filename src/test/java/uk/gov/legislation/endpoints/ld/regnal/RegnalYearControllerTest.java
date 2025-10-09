package uk.gov.legislation.endpoints.ld.regnal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.accept.ContentNegotiationManager;
import uk.gov.legislation.data.virtuoso.queries.RegnalYearQuery;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(requestedType));
        when(query.fetchRawData(reign, regnalYear, requestedType.toString())).thenReturn(rawResult);

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                .accept(acceptHeader))
            .andExpect(status().isOk())
            .andExpect(content().contentType(acceptHeader))
            .andExpect(content().string(rawResult));
    }

    @DisplayName("Should fallback or reject on unsupported media type")
    @Test
    void shouldHandleUnsupportedMediaType() throws Exception {
        MediaType requestedType = MediaType.valueOf("application/x-custom");

        when(negotiation.resolveMediaTypes(any())).thenReturn(List.of(requestedType));
        when(query.fetchRawData(reign, regnalYear, requestedType.toString())).thenReturn(rawResult);

        mockMvc.perform(get("/ld/regnal/{reign}/{regnalYear}", reign, regnalYear)
                    .accept(requestedType))
                .andExpect(status().isNotAcceptable());
        }

    }