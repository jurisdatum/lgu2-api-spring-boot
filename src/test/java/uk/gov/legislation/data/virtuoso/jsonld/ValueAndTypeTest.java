package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueAndTypeTest {

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
    void testConvert_WithObjectNode() {

        ObjectNode objectNode = new ObjectMapper().createObjectNode()
            .put("@value", "mocked value")
            .put("@type", "mockedType");

        ValueAndType result = ValueAndType.convert(objectNode);

        assertAll("ObjectNode conversion",
            () -> assertEquals("mocked value", result.value),
            () -> assertEquals("mockedType", result.type)
        );
    }

}
