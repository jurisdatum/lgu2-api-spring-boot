package uk.gov.legislation.data.virtuoso.jsonld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValueAndLanguageTest {


    @Test
    @DisplayName("Should return value for matching language")
    void testGetReturnsValueForMatchingLanguage() {
        List<ValueAndLanguage> list = List.of(
            create("Value1", "en"),
            create("Value2", "es")
        );

        String result = ValueAndLanguage.get(list, "en");

        assertEquals("Value1", result);
    }

    @Test
    @DisplayName("Should return null when no language matches")
    void testGetReturnsNullWhenNoMatchingLanguage() {
        List<ValueAndLanguage> list = List.of(
            create("Value1", "en"),
            create("Value2", "es")
        );

        String result = ValueAndLanguage.get(list, "fr");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null for empty list")
    void testGetReturnsNullForEmptyList() {
        List<ValueAndLanguage> list = List.of();

        String result = ValueAndLanguage.get(list, "en");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return first matching value if multiple entries match")
    void testGetReturnsFirstMatchingValueForDuplicateLanguages() {
        List<ValueAndLanguage> list = List.of(
            create("Value1", "en"),
            create("Value2", "en")
        );

        String result = ValueAndLanguage.get(list, "en");

        assertEquals("Value1", result);
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testGetHandlesNullValuesGracefully() {
        List<ValueAndLanguage> list = List.of(
            create(null, "en"),
            create("Value2", "es")
        );

        String result = ValueAndLanguage.get(list, "en");

        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null languages gracefully")
    void testGetHandlesNullLanguagesGracefully() {
        List<ValueAndLanguage> list = List.of(
            create("Value1", null),
            create("Value2", "es")
        );

        String result = ValueAndLanguage.get(list, "en");

        assertNull(result);
    }

    //Helper method to reduce repetition
    private ValueAndLanguage create(String value, String language) {
        ValueAndLanguage val = new ValueAndLanguage();
        val.value = value;
        val.language = language;
        return val;
    }

}