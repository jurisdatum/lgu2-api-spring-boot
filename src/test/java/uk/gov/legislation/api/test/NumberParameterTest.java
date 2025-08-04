package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.endpoints.search.NumberAndSeries;

import static org.junit.jupiter.api.Assertions.*;

 class NumberParameterTest {

    @Test
    void testNullInputReturnsNull() {
        assertTrue(NumberAndSeries.parse(null).isEmpty());
    }

    @Test
    void testBlankInputReturnsNull() {
        assertTrue(NumberAndSeries.parse("  ").isEmpty());
    }

    @Test
    void testValidNiSeries() {
        NumberAndSeries result = NumberAndSeries.parse("ni123").get();
        assertEquals(123, result.number());
        assertEquals(Parameters.Series.NI, result.series());
    }

    @Test
    void testValidSingleCharSeriesW() {
        NumberAndSeries result = NumberAndSeries.parse("w45").get();
        assertEquals(45, result.number());
        assertEquals(Parameters.Series.W, result.series());
    }

    @Test
    void testValidSingleCharSeriesS() {
        NumberAndSeries result = NumberAndSeries.parse("s99").get();
        assertEquals(99, result.number());
        assertEquals(Parameters.Series.S, result.series());
    }

    @Test
    void testValidSingleCharSeriesC() {
        NumberAndSeries result = NumberAndSeries.parse("c10").get();
        assertEquals(10, result.number());
        assertEquals(Parameters.Series.C, result.series());
    }

    @Test
    void testValidSingleCharSeriesL() {
        NumberAndSeries result = NumberAndSeries.parse("l77").get();
        assertEquals(77, result.number());
        assertEquals(Parameters.Series.L, result.series());
    }

    @Test
    void testWhitespaceAroundNumberHandledCorrectly() {
        String input = "w  123 ";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> NumberAndSeries.parse(input),
            "Expected input to throw Exception: " + input
        );
        assertNotNull(exception.getMessage());
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
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> NumberAndSeries.parse(input),
            "Expected input to throw Exception: " + input
        );
        assertNotNull(exception.getMessage());
    }
}
