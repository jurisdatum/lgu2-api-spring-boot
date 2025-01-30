package uk.gov.legislation.api.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class FeedConversionTest {

    private final ObjectMapper mapper = UnappliedEffectsTest.mapper;

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
        PageOfDocuments response = DocumentsFeedConverter.convert(results);
        String actual = mapper.writeValueAsString(response);
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
        PageOfDocuments response = DocumentsFeedConverter.convert(results);
        String actual = mapper.writeValueAsString(response);
        Assertions.assertEquals(expected, actual);
    }

}
