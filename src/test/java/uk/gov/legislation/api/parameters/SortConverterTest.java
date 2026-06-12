package uk.gov.legislation.api.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.search.Parameters;

class SortConverterTest {

    private final SortConverter converter = new SortConverter();

    @Test
    void convertsWireValuesUsingEnumParser() {
        assertEquals(Parameters.Sort.PUBLISHED, converter.convert("published"));
        assertEquals(Parameters.Sort.TITLE, converter.convert("  TiTlE  "));
    }

    @Test
    void sortUsesWireValueForSerialization() {
        assertEquals("relevance", Parameters.Sort.RELEVANCE.value());
        assertEquals("relevance", Parameters.Sort.RELEVANCE.toString());
    }

    @Test
    void rejectsUnknownWireValues() {
        assertThrows(IllegalArgumentException.class, () -> Parameters.Sort.fromValue("newest"));
    }
}
