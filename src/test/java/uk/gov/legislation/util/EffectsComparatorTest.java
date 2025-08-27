package uk.gov.legislation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.legislation.util.EffectsComparator.compareFragmentIds;

class EffectsComparatorTest {


    @ParameterizedTest(name = "[{index}] {0} vs {1} => expected {2}")
    @MethodSource("provideFragmentIdComparisons")
    @DisplayName("compareFragmentIds should correctly compare various ID patterns")
    void testCompareFragmentIds(String id1, String id2, int expectedSign) {
        int result = compareFragmentIds(id1, id2);
        assertEquals(expectedSign, Integer.signum(result),
            String.format("Expected comparison of '%s' vs '%s' to return %d but got %d", id1, id2, expectedSign, Integer.signum(result)));
    }

    static Stream<Arguments> provideFragmentIdComparisons() {
        return Stream.of(
            // Identical
            Arguments.of("act-part-section-1", "act-part-section-1", 0),

            // Lexicographical numeric order
            Arguments.of("act-part-1", "act-part-2", -1),
            Arguments.of("act-part-3", "act-part-2", 1),

            // Extra hyphen ignored
            Arguments.of("act--part-2", "act-part-2", 0),

            // Token ranking
            Arguments.of("act-part", "act-section", -1),

            // Numeric comparisons
            Arguments.of("act-part-section-10", "act-part-section-2", 1),
            Arguments.of("act-part-2A", "act-part-2", 1),
            Arguments.of("act-part-001", "act-part-01", 0),
            Arguments.of("001", "1", 0),

            // Alpha comparisons
            Arguments.of("act-a", "act-b", -1),

            // Case-insensitive: section == Section
            Arguments.of("act-part-Section", "act-part-section", -1),

            // Length differences
            Arguments.of("act-part-1", "act-part-1-section", -1),

            // Tokens not in mapping are ignored → compared only up to available mapped tokens
            Arguments.of("act-part-1", "act-part-1-subpart", 0),

            // Alphanumeric suffixes
            Arguments.of("act-part-1A", "act-part-1B", -1),
            Arguments.of("act-part-10Z", "act-part-10AA", -1),  // Single letter < double letter

            // Specific token filtering
            Arguments.of("act-part-title-cross", "act-part-title", 0)
        );
    }

}