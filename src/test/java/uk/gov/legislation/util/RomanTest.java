package uk.gov.legislation.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RomanTest {

    @ParameterizedTest(name = "Roman numeral for {0} should be {1}")
    @MethodSource("validRomanInputs")
    void toUpperRoman_ValidInputs_ReturnExpectedRomanNumeral(int input, String expected) {
        assertEquals(expected, Roman.toUpperRoman(input));
    }

    static Stream<Arguments> validRomanInputs() {
        return Stream.of(
            arguments(1, "I"),             // Minimum valid input
            arguments(4, "IV"),            // Small value
            arguments(1987, "MCMLXXXVII"), // Mid-range
            arguments(3999, "MMMCMXCIX")   // Maximum valid input
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4000}) // Invalid cases: below and above valid range
    void toUpperRoman_InvalidInputs_ThrowIllegalArgumentException(int invalidInput) {
        assertThrows(IllegalArgumentException.class, () -> Roman.toUpperRoman(invalidInput));
    }
}