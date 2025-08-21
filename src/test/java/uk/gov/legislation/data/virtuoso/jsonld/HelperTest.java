package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelperTest {

    @Test
    @DisplayName("Should wrap single node into a list")
    void testOneOrManyWithSingleNode() {

        String expectedValue = "TestValue";
        JsonNode singleNode = new ObjectMapper().valueToTree(expectedValue);

        List<String> result = Helper.oneOrMany(singleNode, String.class);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size()),
            () -> assertEquals(expectedValue, result.getFirst())
        );
    }

    @Test
    @DisplayName("Should convert array node into list of values")
    void testOneOrManyWithArrayNode() {

        ObjectMapper realMapper = new ObjectMapper();
        ArrayNode arrayNode = realMapper.createArrayNode();

        String expectedItem1 = "Item1";
        String expectedItem2 = "Item2";

        JsonNode item1 = realMapper.valueToTree(expectedItem1);
        JsonNode item2 = realMapper.valueToTree(expectedItem2);

        arrayNode.add(item1);
        arrayNode.add(item2);

        List<String> result = Helper.oneOrMany(arrayNode, String.class);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.size()),
            () -> assertEquals("Item1", result.getFirst()),
            () -> assertEquals("Item2", result.get(1)),
            () -> assertEquals(expectedItem2, result.get(1))
        );
    }

    @Test
    @DisplayName("Should return empty list for empty array node")
    void testOneOrManyWithEmptyArrayNode() {
        ArrayNode emptyArrayNode = new ObjectMapper().createArrayNode();

        List<String> result = Helper.oneOrMany(emptyArrayNode, String.class);

        assertAll(
            () -> assertNotNull(result),
            () -> assertTrue(result.isEmpty(), "Result should be an empty list.")
        );
    }

    @Test
    @DisplayName("Should return singleton list with null for null input node")
    void testOneOrManyWithNullNodeReturnsSingletonNull() {
        List<String> result = Helper.oneOrMany(null, String.class);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(1, result.size(), "Result should contain one element."),
            () -> assertNull(result.getFirst(), "Element should be null.")
        );
    }

}
