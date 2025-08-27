package uk.gov.legislation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RegnalYearTest {

    // parse(...) tests

    @Test
    void parse_ValidInput_ReturnsRegnalYearInstance() {
        String input = "20/Elizabeth";
        RegnalYear result = RegnalYear.parse(input);

        assertNotNull(result);
        assertEquals("20/Elizabeth", result.toString());
    }

    @Test
    void parse_InputWithUnderscoreDelimiter_ReturnsRegnalYearInstance() {
        String input = "5_George";
        RegnalYear result = RegnalYear.parse(input);

        assertNotNull(result);
        assertEquals("5/George", result.toString().replace("_", "/"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"InvalidInput", "", "OnlyOnePart"})
    void parse_InvalidInputs_ThrowIllegalArgumentException(String input) {
        assertThrows(IllegalArgumentException.class, () -> RegnalYear.parse(input));
    }

    @Test
    void parse_InputWithMultipleParts_ReturnsCorrectResult() {
        String input = "10/George/V";
        RegnalYear result = RegnalYear.parse(input);

        assertNotNull(result);
        assertEquals("10/George/V", result.toString());
    }

    // combineYears(...) tests

    @ParameterizedTest
    @MethodSource("combineYearsProvider")
    void combineYears_ProcessesCorrectly(String[] input, String[] expected) {
        assertArrayEquals(expected, RegnalYear.combineYears(input));
    }

    static Stream<Arguments> combineYearsProvider() {
        return Stream.of(
            arguments(new String[]{}, new String[]{}),
            arguments(new String[]{"10", "George", "V"}, new String[]{"10", "George", "V"}),
            arguments(new String[]{"10", "and", "20", "George", "VI"}, new String[]{"10 & 20", "George", "VI"}),
            arguments(new String[]{"5", "and", "10", "George", "and", "15", "Elizabeth"},
                new String[]{"5 & 10", "George", "and", "15", "Elizabeth"})
        );
    }

    // addPunctuation(...) tests

    @ParameterizedTest
    @MethodSource("addPunctuationProvider")
    void addPunctuation_ProcessesCorrectly(String[] input, String[] expected) {
        RegnalYear.addPunctuation(input);
        assertArrayEquals(expected, input);
    }

    static Stream<Arguments> addPunctuationProvider() {
        return Stream.of(
            arguments(new String[]{}, new String[]{}),
            arguments(new String[]{"20", "and", "30"}, new String[]{"20", "and", "30"}),
            arguments(new String[]{"Elizabeth", "George"}, new String[]{"Elizabeth.", "George."}),
            arguments(new String[]{"10", "George"}, new String[]{"10", "George."})
        );
    }

    // forCitation() tests

    @ParameterizedTest
    @MethodSource("forCitationProvider")
    void forCitation_ReturnsFormattedOutput(String input, String expected) {
        RegnalYear regnalYear = RegnalYear.parse(input);
        assertEquals(expected, regnalYear.forCitation());
    }

    static Stream<Arguments> forCitationProvider() {
        return Stream.of(
            arguments("10/George/V", "(10 George. V)"),
            arguments("10_and_20/George", "(10 & 20 George.)"),
            arguments("10/Elizabeth", "(10 Elizabeth.)"),
            arguments("10/Elizabeth_and_George", "(10 Elizabeth. and George.)"),
            arguments("10_and_20", "(10 & 20)"),
            arguments("10/Elizabeth_and_20/George", "(10 Elizabeth. and 20 George.)")
        );
    }

    @Test
    void forCitation_EmptyInput_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> RegnalYear.parse(""));
    }
}