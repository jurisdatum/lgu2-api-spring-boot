package uk.gov.legislation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VersionsTest {


    @ParameterizedTest
    @ValueSource(strings = {
        "enacted",
        "made",
        "created",
        "adopted",
        "current",
        "prospective", "2025-12-25"})
    void isVersionLabel_ValidInputs_ReturnsTrue(String input) {
        assertTrue(Versions.isVersionLabel(input),
            "Expected '" + input + "' to be recognized as a valid version label.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "25-12-2025", "randomString", ""})
    void isVersionLabel_InvalidInputs_ReturnsFalse(String input) {
        assertFalse(Versions.isVersionLabel(input),
            "Expected '" + input + "' to not be recognized as a valid version label.");
    }

    @Test
    void isVersionLabel_NullInput_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Versions.isVersionLabel(null));
    }
}