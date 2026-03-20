package uk.gov.legislation.api.parameters;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.search.Parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SortConverterTest {

    private final SortConverter converter = new SortConverter();
    private final SortConverter.EffectsSortConverter effectSortConverter= new SortConverter.EffectsSortConverter();

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

    @Test
    void effectSortConvertsWireValuesUsingEnumParser() {
        assertEquals(uk.gov.legislation.data.marklogic.changes.Parameters.EffectsSort.SourceTitle,
            effectSortConverter.convert("affecting-title"));

        assertEquals(uk.gov.legislation.data.marklogic.changes.Parameters.EffectsSort.SourceTitle,
            effectSortConverter.convert("  AfFeCtInG-TiTlE  "));
    }

    @Test
    void enumParserRejectsUnknownWireValues() {
        assertThrows(IllegalArgumentException.class,
            () -> effectSortConverter.convert("unknown-sort"));
    }
    }
