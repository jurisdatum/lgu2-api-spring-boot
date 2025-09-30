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
            Arguments.of("section-1", "section-1", 0),
            Arguments.of("part-1", "part-1", 0),

            // Lexicographical numeric order
            Arguments.of("part-1", "part-2", -1),
            Arguments.of("part-3", "part-2", 1),

            // Extra hyphen ignored
            Arguments.of("section--2", "section-2", 0),

            // Token ranking
            Arguments.of("part-1", "section-1", -1),

            // Numeric comparisons
            Arguments.of("section-10", "section-2", 1),
            Arguments.of("section-2A", "section-2", 1),
            Arguments.of("section-01", "section-1", 0),
            Arguments.of("part-01", "part-1", 0),

            // Alpha comparisons
            Arguments.of("part-a", "part-b", -1),

            // Case-insensitive: a == A
            // TODO what's this about
//            Arguments.of("part-a", "part-A", 0),

            // Length differences
            Arguments.of("section-1", "section-1-1", -1),

            // Tokens not in mapping are ignored → compared only up to available mapped tokens
            Arguments.of("part-1", "part-1-subpart", 0),

            // Alphanumeric suffixes
            Arguments.of("section-1A", "section-1B", -1),
            Arguments.of("section-10Z", "section-10AA", -1),  // Single letter < double letter

            // Specific token filtering
            Arguments.of("part-1-crossheading-bulk-personal-dataset-warrants", "part-1-crossheading-low-or-no-reasonable-expectation-of-privacy", 0)
        );
    }

}