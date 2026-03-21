package uk.gov.legislation.api.parameters;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.changes.Parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EffectsSortConverterTest {

    private final EffectsSortConverter converter = new EffectsSortConverter();

    @Test
    void convertsWireValuesUsingEnumParser() {
        assertEquals(Parameters.EffectsSort.SourceTitle, converter.convert("affecting-title"));
        assertEquals(Parameters.EffectsSort.SourceTitle, converter.convert("  AfFeCtInG-TiTlE  "));
    }

    @Test
    void rejectsUnknownWireValues() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("unknown-sort"));
    }

}
