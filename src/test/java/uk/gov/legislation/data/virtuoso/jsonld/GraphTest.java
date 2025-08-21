package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    @DisplayName("Should extract valid graph from JSON with one object")
    void testExtractWithValidJsonContainingGraph() throws JsonProcessingException {
        String validJson = "{ \"@graph\": [{ \"id\": \"1\", \"name\": \"Test\" }] }";

        ArrayNode result = Graph.extract(validJson);

        assertAll(
            () -> assertNotNull(result, "The extracted graph should not be null."),
            () -> assertEquals(1, result.size(), "The graph should contain one element."),
            () -> assertEquals("1", result.get(0).get("id").asText(), "The id of the first element should be '1'."),
            () -> assertEquals("Test", result.get(0).get("name").asText(), "The name of the first element should be 'Test'.")
        );
    }

    @Test
    @DisplayName("Should extract empty graph from valid JSON")
    void testExtractWithValidJsonEmptyGraph() throws JsonProcessingException {
        String validJson = "{ \"@graph\": [] }";

        ArrayNode result = Graph.extract(validJson);

        assertAll(
            () -> assertNotNull(result, "The extracted graph should not be null."),
            () -> assertEquals(0, result.size(), "The graph should be empty.")
        );
    }

    @Test
    @DisplayName("Should throw exception for invalid JSON in extract()")
    void testExtractWithInvalidJson() {
        String invalidJson = "{ invalid json ";

        assertThrows(JsonProcessingException.class,
            () -> Graph.extract(invalidJson),
            "Passing invalid JSON should throw a JsonProcessingException.");
    }

    @ParameterizedTest(name = "{index} => JSON: {0}, expectedId: {1}, expectedName: {2}")
    @MethodSource("validGraphObjectProvider")
    @DisplayName("Should extract first object from valid JSON graph")
    void testExtractFirstObjectWithValidJson(String json, String expectedId, String expectedName) throws JsonProcessingException {

        Optional<JsonNode> result = Graph.extractFirstObject(json, JsonNode.class);

        assertAll(
            () -> assertTrue(result.isPresent(), "The first object should be present."),
            () -> assertEquals(expectedId, result.get().get("id").asText(), "The id of the first object should match."),
            () -> assertEquals(expectedName, result.get().get("name").asText(), "The name of the first object " +
                "should match.")
        );
    }

    static Stream<Arguments> validGraphObjectProvider() {
        return Stream.of(
            Arguments.of(
                "{ \"@graph\": [{ \"id\": \"1\", \"name\": \"Test\" }] }",
                "1", "Test"
            )
        );
    }

    @Test
    @DisplayName("Should return empty Optional for empty graph in extractFirstObject")
    void testExtractFirstObjectWithEmptyGraph() throws JsonProcessingException {
        String validJson = "{ \"@graph\": [] }";

        Optional<JsonNode> result = Graph.extractFirstObject(validJson, JsonNode.class);

        assertFalse(result.isPresent(), "No object should be extracted from an empty graph.");
    }

    @Test
    @DisplayName("Should throw exception for invalid JSON in extractFirstObject")
    void testExtractFirstObjectWithInvalidJson() {
        String invalidJson = "{ invalid json ";

        assertThrows(JsonProcessingException.class,
            () -> Graph.extractFirstObject(invalidJson, JsonNode.class),
            "Passing invalid JSON should throw a JsonProcessingException.");
    }
}