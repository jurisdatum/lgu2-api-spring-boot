package uk.gov.legislation.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import uk.gov.legislation.api.responses.RichText;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.util.Links;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RichTextConverterTest {

    @ParameterizedTest(name = "{index} => case: {0}")
    @MethodSource("provideInvalidInputs")
    @DisplayName("Should handle null/empty/unrecognized inputs gracefully")
    void testConvert_InvalidInputs(String caseName, List<RichTextNode> input, Executable assertion) {
        assertAll(caseName, assertion);
    }

    static Stream<Arguments> provideInvalidInputs() {
        RichTextNode unrecognizedNode = mock(RichTextNode.class);
        return Stream.of(
            Arguments.of(
                "Empty input list",
                List.of(),
                (Executable) () -> assertTrue(RichTextConverter.convert(List.of()).isEmpty())
            ),
            Arguments.of(
                "Null input list",
                null,
                (Executable) () -> assertThrows(NullPointerException.class, () -> RichTextConverter.convert(null))
            ),
            Arguments.of(
                "Range node with no children",
                List.of(newRangeNode(List.of())),
                (Executable) () -> assertTrue(RichTextConverter.convert(List.of(newRangeNode(List.of()))).isEmpty())
            ),
            Arguments.of(
                "Unrecognized node type",
                List.of(unrecognizedNode),
                (Executable) () -> assertEquals(List.of(), RichTextConverter.convert(List.of(unrecognizedNode)))
            )
        );
    }

    @Test
    @DisplayName("Should convert text node correctly")
    void testConvert_TextNode() {
        RichTextNode.Text textNode = new RichTextNode.Text();
        textNode.text = "Sample text";

        List<RichText.Node> result = RichTextConverter.convert(List.of(textNode));

        assertEquals(1, result.size());
        RichText.Node node = result.getFirst();

        assertAll("Text Node",
            () -> assertEquals("text", node.type),
            () -> assertEquals("Sample text", node.text),
            () -> assertNull(node.id),
            () -> assertNull(node.href),
            () -> assertNull(node.missing)
        );
    }

    @Test
    @DisplayName("Should convert section node with mocked shortened URI")
    void testConvert_SectionNode() {
        RichTextNode.Section sectionNode = new RichTextNode.Section();
        sectionNode.text = "Section text";
        sectionNode.ref = "section-1";
        sectionNode.uri = "http://example.com/section-1";
        sectionNode.missing = true;

        try (MockedStatic<Links> mockedLinks = mockStatic(Links.class)) {
            mockedLinks.when(() -> Links.shorten("http://example.com/section-1"))
                .thenReturn("shortened/section-1");

            List<RichText.Node> result = RichTextConverter.convert(List.of(sectionNode));

            assertEquals(1, result.size());
            RichText.Node node = result.getFirst();

            assertAll("Section Node",
                () -> assertEquals("link", node.type),
                () -> assertEquals("Section text", node.text),
                () -> assertEquals("section-1", node.id),
                () -> assertEquals("shortened/section-1", node.href),
                () -> assertTrue(node.missing)
            );
        }
    }

    @Test
    @DisplayName("Should convert range node containing text child")
    void testConvert_RangeNodeWithTextChild() {
        RichTextNode.Text text = new RichTextNode.Text();
        text.text = "Child text";

        RichTextNode.Range range = newRangeNode(List.of(text));
        List<RichText.Node> result = RichTextConverter.convert(List.of(range));

        assertEquals(1, result.size());
        RichText.Node node = result.getFirst();

        assertAll("Range -> Text",
            () -> assertEquals("text", node.type),
            () -> assertEquals("Child text", node.text),
            () -> assertNull(node.id),
            () -> assertNull(node.href),
            () -> assertNull(node.missing)
        );
    }

    @Test
    @DisplayName("Should convert range node with multiple children")
    void testConvert_RangeNodeWithMultipleChildren() {
        RichTextNode.Text textChild = new RichTextNode.Text();
        textChild.text = "Child text";

        RichTextNode.Section sectionChild = new RichTextNode.Section();
        sectionChild.text = "Section child";
        sectionChild.ref = "section-2";
        sectionChild.uri = "http://example.com/section-2";

        try (MockedStatic<Links> mockedLinks = mockStatic(Links.class)) {
            mockedLinks.when(() -> Links.shorten("http://example.com/section-2"))
                .thenReturn("http://example.com/section-2");

            RichTextNode.Range range = newRangeNode(List.of(textChild, sectionChild));
            List<RichText.Node> result = RichTextConverter.convert(List.of(range));

            assertEquals(2, result.size());

            RichText.Node textNode = result.get(0);
            assertAll("Text child",
                () -> assertEquals("text", textNode.type),
                () -> assertEquals("Child text", textNode.text),
                () -> assertNull(textNode.id),
                () -> assertNull(textNode.href)
            );

            RichText.Node sectionNode = result.get(1);
            assertAll("Section child",
                () -> assertEquals("link", sectionNode.type),
                () -> assertEquals("Section child", sectionNode.text),
                () -> assertEquals("section-2", sectionNode.id),
                () -> assertEquals("http://example.com/section-2", sectionNode.href)
            );
        }
    }

    // Helper method for Range node
    private static RichTextNode.Range newRangeNode(List<RichTextNode> children) {
        RichTextNode.Range range = new RichTextNode.Range();
        range.children = children;
        return range;
    }
}