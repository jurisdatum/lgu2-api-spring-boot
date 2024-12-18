package uk.gov.legislation.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.endpoints.documents.Converter;
import uk.gov.legislation.endpoints.documents.DocumentList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FeedConversionTest {

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void ukpga() throws IOException {
        String atom;
        try (var input = getClass().getResourceAsStream("/ukpga.feed")) {
            Objects.requireNonNull(input);
            atom = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String expected;
        try (var input = getClass().getResourceAsStream("/ukpga.json")) {
            Objects.requireNonNull(input);
            expected = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        SearchResults results = SearchResults.parse(atom);
        DocumentList list = Converter.convert(results);
        ObjectMapper mapper = new ObjectMapper()
            .registerModules(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String actual = mapper.writeValueAsString(list);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void uksi() throws IOException {
        String atom;
        try (var input = getClass().getResourceAsStream("/uksi.feed")) {
            Objects.requireNonNull(input);
            atom = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String expected;
        try (var input = getClass().getResourceAsStream("/uksi.json")) {
            Objects.requireNonNull(input);
            expected = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        SearchResults results = SearchResults.parse(atom);
        DocumentList list = Converter.convert(results);
        String actual = mapper.writeValueAsString(list);
        Assertions.assertEquals(expected, actual);
    }

}
