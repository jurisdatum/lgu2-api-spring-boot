package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.legislation.util.NumberSeries;

import static org.junit.jupiter.api.Assertions.*;

 class NumberSeriesTest {

    @Test
    void testNullInputReturnsNull() {
        assertNull(NumberSeries.extractSeriesFromNumber(null));
    }

    @Test
    void testBlankInputReturnsNull() {
        assertNull(NumberSeries.extractSeriesFromNumber("  "));
    }

    @Test
    void testValidNiSeries() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("ni123");
        assertEquals(123, result.number());
        assertEquals("ni", result.series());
    }

    @Test
    void testValidSingleCharSeriesW() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("w45");
        assertEquals(45, result.number());
        assertEquals("w", result.series());
    }

    @Test
    void testValidSingleCharSeriesS() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("s99");
        assertEquals(99, result.number());
        assertEquals("s", result.series());
    }

    @Test
    void testValidSingleCharSeriesC() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("c10");
        assertEquals(10, result.number());
        assertEquals("c", result.series());
    }

    @Test
    void testValidSingleCharSeriesL() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("l77");
        assertEquals(77, result.number());
        assertEquals("l", result.series());
    }

    @Test
    void testWhitespaceAroundNumberHandledCorrectly() {
        NumberSeries result = NumberSeries.extractSeriesFromNumber("w  123 ");
        assertEquals(123, result.number());
        assertEquals("w", result.series());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "x123",   // invalid prefix
        "ni",     // missing number
        "niabc",  // non-numeric number
        "w ",     // empty number
        "s",      // missing number
        "lXYZ",   // non-numeric
        "c  ",    // blank number
        "ni  ",   // trailing spaces only
        "w--12"   // malformed number
    })
    void testInvalidInputsThrowNumberFormatException(String input) {
        NumberFormatException exception = assertThrows(
            NumberFormatException.class,
            () -> NumberSeries.extractSeriesFromNumber(input),
            "Expected input to throw NumberFormatException: " + input
        );
        assertNotNull(exception.getMessage());
    }
}


