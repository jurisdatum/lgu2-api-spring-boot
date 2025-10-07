package uk.gov.legislation.data.virtuoso.queries;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.data.virtuoso.Virtuoso;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterpretationQueryTest {

    /**
     * Helper method to read test JSON files from resources
     */
    private String readTestFile(String filename) throws IOException {
        String path = "src/test/resources/virtuoso/" + filename;
        return Files.readString(Paths.get(path));
    }

    /**
     * Test parsing of flat format (Virtuoso 7.x) through the actual InterpretationQuery.get() method.
     * This exercises the real branching logic for graph.size() == 2.
     */
    @Test
    void testParseFlatFormat() throws Exception {
        // Given: Mock Virtuoso that returns flat format JSON
        String json = readTestFile("flat-interpretation-uksi-2001-1.json");
        Virtuoso mockVirtuoso = mock(Virtuoso.class);
        when(mockVirtuoso.query(anyString(), eq("application/ld+json"))).thenReturn(json);

        // When: Call the actual entry point
        InterpretationQuery query = new InterpretationQuery(mockVirtuoso);
        Optional<Interpretation> result = query.get("uksi", "2001", "1", null, false);

        // Then: Should successfully parse and return interpretation with item
        assertTrue(result.isPresent(), "Should return interpretation for flat format");
        Interpretation interpretation = result.get();
        assertNotNull(interpretation, "Interpretation should not be null");
        assertNotNull(interpretation.item, "Item should be attached");
        assertEquals("http://www.legislation.gov.uk/uksi/2001/1", interpretation.uri.toString());
        assertEquals("http://www.legislation.gov.uk/id/uksi/2001/1", interpretation.item.uri.toString());
        assertEquals(2001, interpretation.item.year);
        assertEquals(1, interpretation.item.number);
    }

    /**
     * Test parsing of nested format (Virtuoso 8.3.x) through the actual InterpretationQuery.get() method.
     * This exercises the real branching logic for graph.size() == 1.
     */
    @Test
    void testParseNestedFormat() throws Exception {
        // Given: Mock Virtuoso that returns nested format JSON
        String json = readTestFile("nested-interpretation-uksi-2001-1.json");
        Virtuoso mockVirtuoso = mock(Virtuoso.class);
        when(mockVirtuoso.query(anyString(), eq("application/ld+json"))).thenReturn(json);

        // When: Call the actual entry point
        InterpretationQuery query = new InterpretationQuery(mockVirtuoso);
        Optional<Interpretation> result = query.get("uksi", "2001", "1", null, false);

        // Then: Should successfully parse and return interpretation with item
        assertTrue(result.isPresent(), "Should return interpretation for nested format");
        Interpretation interpretation = result.get();
        assertNotNull(interpretation, "Interpretation should not be null");
        assertNotNull(interpretation.item, "Item should be attached");
        assertEquals("http://www.legislation.gov.uk/uksi/2001/1", interpretation.uri.toString());
        assertEquals("http://www.legislation.gov.uk/id/uksi/2001/1", interpretation.item.uri.toString());
        assertEquals(2001, interpretation.item.year);
        assertEquals(1, interpretation.item.number);
    }

    /**
     * Test that unexpected numbers of Interpretations/Items throw IllegalStateException.
     * This verifies the fail-fast behavior - we expect exactly 1 Interpretation and 1 Item.
     */
    @Test
    void testUnexpectedStructureThrows() throws Exception {
        // Given: Mock Virtuoso that returns 2 Interpretations and 1 Item (invalid)
        String invalidJson = """
            {
              "@context": {},
              "@graph": [
                {
                  "@id": "http://www.legislation.gov.uk/uksi/2001/1",
                  "@type": ["http://www.legislation.gov.uk/def/legislation/Interpretation"]
                },
                {
                  "@id": "http://www.legislation.gov.uk/uksi/2001/1/made",
                  "@type": ["http://www.legislation.gov.uk/def/legislation/Interpretation"]
                },
                {
                  "@id": "http://www.legislation.gov.uk/id/uksi/2001/1",
                  "@type": ["http://www.legislation.gov.uk/def/legislation/Item"]
                }
              ]
            }
            """;
        Virtuoso mockVirtuoso = mock(Virtuoso.class);
        when(mockVirtuoso.query(anyString(), eq("application/ld+json"))).thenReturn(invalidJson);

        // When/Then: Should throw IllegalStateException
        InterpretationQuery query = new InterpretationQuery(mockVirtuoso);
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> query.get("uksi", "2001", "1", null, false),
            "Should throw when finding 2 Interpretations instead of 1"
        );
        assertTrue(exception.getMessage().contains("Unexpected Interpretation JSON-LD format"));
    }
}
