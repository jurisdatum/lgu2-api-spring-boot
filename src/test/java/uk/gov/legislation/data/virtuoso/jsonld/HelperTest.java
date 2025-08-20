package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelperTest {


    @Test
    @DisplayName("Should wrap single node into a list")
    void testOneOrManyWithSingleNode() {
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        Graph.mapper = objectMapperMock;

        JsonNode singleNode = mock(JsonNode.class);
        String expectedValue = "TestValue";

        when(objectMapperMock.convertValue(singleNode, String.class)).thenReturn(expectedValue);

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
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        Graph.mapper = objectMapperMock;

        ObjectMapper realMapper = new ObjectMapper();
        ArrayNode arrayNode = realMapper.createArrayNode();

        ObjectNode item1 = realMapper.createObjectNode().put("value", "1");
        ObjectNode item2 = realMapper.createObjectNode().put("value", "2");

        arrayNode.add(item1);
        arrayNode.add(item2);

        String convertedItem1 = "Item1";
        String convertedItem2 = "Item2";

        when(objectMapperMock.convertValue(item1, String.class)).thenReturn(convertedItem1);
        when(objectMapperMock.convertValue(item2, String.class)).thenReturn(convertedItem2);
        List<String> result = Helper.oneOrMany(arrayNode, String.class);

        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(2, result.size()),
            () -> assertEquals("Item1", result.getFirst()),
            () -> assertEquals("Item2", result.get(1)),
            () -> assertEquals(convertedItem2, result.get(1))
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