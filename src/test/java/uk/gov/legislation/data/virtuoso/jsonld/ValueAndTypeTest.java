package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValueAndTypeTest {

    @BeforeEach
    void setup() {
        Graph.mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("convert() returns correct ValueAndType for TextNode")
    void testConvert_WithTextNode() {

        JsonNode textNode = new TextNode("example text");
        ValueAndType result = ValueAndType.convert(textNode);

        assertAll("TextNode conversion",
            () -> assertEquals("example text", result.value),
            () -> assertEquals("http://www.w3.org/2001/XMLSchema#string", result.type)
        );
    }


    @Test
    @DisplayName("convert() delegates to ObjectMapper for non-TextNode JsonNode")
    void testConvert_WithMockedNode() {

        JsonNode mockedNode = mock(JsonNode.class);
        when(mockedNode.isTextual()).thenReturn(false);
        when(mockedNode.isObject()).thenReturn(true);

        ValueAndType mockConversion = new ValueAndType();
        mockConversion.value = "mocked value";
        mockConversion.type = "mockedType";

        ObjectMapper mockedMapper = mock(ObjectMapper.class);
        Graph.mapper = mockedMapper;

        when(mockedMapper.convertValue(mockedNode, ValueAndType.class)).thenReturn(mockConversion);

        ValueAndType result = ValueAndType.convert(mockedNode);

        assertAll("Mocked conversion",
            () -> assertEquals("mocked value", result.value),
            () -> assertEquals("mockedType", result.type)
        );
        verify(mockedMapper, times(1)).convertValue(mockedNode, ValueAndType.class);
    }
}