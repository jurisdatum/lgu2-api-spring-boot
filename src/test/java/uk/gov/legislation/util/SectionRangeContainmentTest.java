package uk.gov.legislation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SectionRangeContainmentTest {

    @Nested
    @DisplayName("contains: sibling ranges at the same structural depth")
    class SiblingRanges {

        @Test
        @DisplayName("numeric subsections: section-249-1 to section-249-3")
        void numericSubsections() {
            String start = "section-249-1";
            String end = "section-249-3";
            assertTrue(contains(start, end, "section-249-1"));   // start boundary
            assertTrue(contains(start, end, "section-249-2"));   // middle
            assertTrue(contains(start, end, "section-249-3"));   // end boundary
            assertFalse(contains(start, end, "section-249-4"));  // after
            assertFalse(contains(start, end, "section-248-5"));  // different parent
        }

        @Test
        @DisplayName("schedule paragraphs: schedule-14-paragraph-1 to schedule-14-paragraph-3")
        void scheduleParagraphs() {
            String start = "schedule-14-paragraph-1";
            String end = "schedule-14-paragraph-3";
            assertTrue(contains(start, end, "schedule-14-paragraph-1"));
            assertTrue(contains(start, end, "schedule-14-paragraph-2"));
            assertTrue(contains(start, end, "schedule-14-paragraph-3"));
            assertFalse(contains(start, end, "schedule-14-paragraph-4"));
            assertFalse(contains(start, end, "schedule-13-paragraph-2"));
        }

        @Test
        @DisplayName("alphabetic sub-paragraphs: section-395-13-bc to section-395-13-be")
        void alphabeticSubParagraphs() {
            String start = "section-395-13-bc";
            String end = "section-395-13-be";
            assertTrue(contains(start, end, "section-395-13-bc"));
            assertTrue(contains(start, end, "section-395-13-bd"));
            assertTrue(contains(start, end, "section-395-13-be"));
            assertFalse(contains(start, end, "section-395-13-bb"));
            assertFalse(contains(start, end, "section-395-13-bf"));
        }

        @Test
        @DisplayName("numeric+alpha suffixes: section-1A to section-1C")
        void numericAlphaSuffix() {
            String start = "section-1A";
            String end = "section-1C";
            assertTrue(contains(start, end, "section-1A"));
            assertTrue(contains(start, end, "section-1B"));
            assertTrue(contains(start, end, "section-1C"));
            assertFalse(contains(start, end, "section-1D"));
            assertFalse(contains(start, end, "section-2"));
        }

        @Test
        @DisplayName("single letter sub-paragraphs: section-1-a to section-1-c")
        void singleLetterSubParagraphs() {
            String start = "section-1-a";
            String end = "section-1-c";
            assertTrue(contains(start, end, "section-1-a"));
            assertTrue(contains(start, end, "section-1-b"));
            assertTrue(contains(start, end, "section-1-c"));
            assertFalse(contains(start, end, "section-1-d"));
        }

        @Test
        @DisplayName("roman numeral sub-paragraphs: section-1-a-i to section-1-a-iv")
        void romanNumeralSubParagraphs() {
            String start = "section-1-a-i";
            String end = "section-1-a-iv";
            assertTrue(contains(start, end, "section-1-a-i"));
            assertTrue(contains(start, end, "section-1-a-ii"));
            assertTrue(contains(start, end, "section-1-a-iii"));
            assertTrue(contains(start, end, "section-1-a-iv"));
            assertFalse(contains(start, end, "section-1-a-v"));
        }

        @Test
        @DisplayName("roman numerals crossing alphabetic boundary: ix to xi")
        void romanNumeralsCrossingAlphabeticBoundary() {
            // ix (9) to xi (11) — alphabetic ordering would give ix < xi, but
            // x (10) would sort alphabetically AFTER xi, giving wrong result.
            // Roman numeral comparison handles this correctly.
            String start = "section-1-a-ix";
            String end = "section-1-a-xi";
            assertTrue(contains(start, end, "section-1-a-x"));
        }

        @Test
        @DisplayName("single-character roman compared against multi-character: v to ix")
        void singleCharRomanAgainstMultiChar() {
            // v (5) to ix (9) — both consist of roman characters, and at least
            // one is multi-character, so both are treated as roman numerals
            String start = "section-1-a-v";
            String end = "section-1-a-ix";
            assertTrue(contains(start, end, "section-1-a-vi"));
            assertTrue(contains(start, end, "section-1-a-viii"));
            assertFalse(contains(start, end, "section-1-a-x"));
        }

        @Test
        @DisplayName("single-letter roman chars treated as alphabetic paragraph labels")
        void singleLetterRomanCharsAreAlphabetic() {
            // c, l, m are valid roman characters but as single-letter tokens
            // they are paragraph labels — c (3rd) is not between l (12th) and m (13th)
            assertFalse(contains("section-1-l", "section-1-m", "section-1-c"));
            // d (4th) is between c (3rd) and i (9th) alphabetically
            assertTrue(contains("section-1-c", "section-1-i", "section-1-d"));
        }
    }

    @Nested
    @DisplayName("contains: hierarchical containment (id deeper than range)")
    class DescendantContainment {

        @Test
        @DisplayName("id is a child of a provision within the range")
        void childOfProvisionInRange() {
            String start = "part-1";
            String end = "part-3";
            assertTrue(contains(start, end, "part-2-section-5"));
            assertTrue(contains(start, end, "part-1-section-1")); // child of start
            assertTrue(contains(start, end, "part-3-section-9")); // child of end
            assertFalse(contains(start, end, "part-4-section-1"));
        }

        @Test
        @DisplayName("id is a deeper descendant within the range")
        void deeperDescendant() {
            String start = "section-1";
            String end = "section-3";
            assertTrue(contains(start, end, "section-2-a-i"));
        }
    }

    @Nested
    @DisplayName("contains: ancestor containment (id shallower than range)")
    class AncestorContainment {

        @Test
        @DisplayName("id is a parent of the range boundaries")
        void parentOfRange() {
            String start = "section-1-a";
            String end = "section-1-c";
            assertTrue(contains(start, end, "section-1"));
        }

        @Test
        @DisplayName("id is a grandparent of the range boundaries")
        void grandparentOfRange() {
            String start = "section-1-a-i";
            String end = "section-1-a-iii";
            assertTrue(contains(start, end, "section-1"));
            assertTrue(contains(start, end, "section-1-a"));
        }

        @Test
        @DisplayName("id is a parent only if all prefix tokens match")
        void notParentIfDifferentPrefix() {
            String start = "section-1-a";
            String end = "section-1-c";
            assertFalse(contains(start, end, "section-2"));
        }
    }

    @Nested
    @DisplayName("contains: edge cases")
    class EdgeCases {

        @Test
        @DisplayName("different structural keywords are not confused")
        void differentKeywords() {
            String start = "part-1";
            String end = "part-3";
            assertFalse(contains(start, end, "section-5"));
        }

        @Test
        @DisplayName("double hyphens in IDs are handled")
        void doubleHyphens() {
            assertTrue(contains("section--1", "section--3", "section--2"));
        }

        @Test
        @DisplayName("numeric ordering is not lexicographic")
        void numericNotLexicographic() {
            String start = "section-2";
            String end = "section-10";
            assertTrue(contains(start, end, "section-5"));
            assertFalse(contains(start, end, "section-11"));
        }
    }

    @Nested
    @DisplayName("compare: ordering of provision identifiers")
    class CompareTests {

        @ParameterizedTest(name = "[{index}] {0} vs {1} => {2}")
        @MethodSource
        void testCompare(String id1, String id2, int expectedSign) {
            int result = SectionRangeContainment.compare(id1, id2);
            assertEquals(expectedSign, Integer.signum(result),
                String.format("Expected comparison of '%s' vs '%s' to have sign %d but got %d",
                    id1, id2, expectedSign, Integer.signum(result)));
        }

        static Stream<Arguments> testCompare() {
            return Stream.of(
                // identical
                Arguments.of("section-1", "section-1", 0),

                // numeric ordering
                Arguments.of("section-1", "section-2", -1),
                Arguments.of("section-10", "section-2", 1),

                // numeric + alpha suffix
                Arguments.of("section-1", "section-1A", -1),
                Arguments.of("section-1A", "section-1B", -1),

                // alphabetic tokens
                Arguments.of("section-1-a", "section-1-b", -1),
                Arguments.of("section-1-bc", "section-1-be", -1),

                // roman numerals
                Arguments.of("section-1-a-ii", "section-1-a-iv", -1),
                Arguments.of("section-1-a-ix", "section-1-a-xi", -1),

                // shorter ID before longer with same prefix
                Arguments.of("part-2", "part-2-section-1", -1),

                // different keyword at same position
                Arguments.of("chapter-1", "section-1", -1)
            );
        }
    }

    @Nested
    @DisplayName("compareSuffix: inserted provision ordering (section 6.4)")
    class InsertedProvisionOrdering {

        @ParameterizedTest(name = "[{index}] {0} vs {1} => {2}")
        @MethodSource
        @DisplayName("compare: full identifier examples from section 6.4 guidance")
        void testGuidanceCompare(String id1, String id2, int expectedSign) {
            int result = SectionRangeContainment.compare(id1, id2);
            assertEquals(expectedSign, Integer.signum(result),
                String.format("Expected comparison of '%s' vs '%s' to have sign %d but got %d",
                    id1, id2, expectedSign, Integer.signum(result)));
        }

        static Stream<Arguments> testGuidanceCompare() {
            return Stream.of(
                // beginning of a series: ZA1, A1, B1, ... then 1
                Arguments.of("schedule-ZA1", "schedule-A1", -1),
                Arguments.of("schedule-A1", "schedule-B1", -1),
                Arguments.of("schedule-B1", "schedule-1", -1),

                // inserted between existing provisions
                Arguments.of("section-1", "section-1ZA", -1),
                Arguments.of("section-1ZA", "section-1A", -1),
                Arguments.of("section-1A", "section-1AZA", -1),
                Arguments.of("section-1AZA", "section-1AA", -1),

                // do not generate a lower level unless needed
                Arguments.of("section-1AA", "section-1AB", -1),
                Arguments.of("section-1AB", "section-1B", -1),
                Arguments.of("section-1AA", "section-1AAA", -1),
                Arguments.of("section-1AAA", "section-1AB", -1),

                // lettered paragraphs
                Arguments.of("section-1-zza", "section-1-za", -1),
                Arguments.of("section-1-za", "section-1-a", -1),
                Arguments.of("section-1-a", "section-1-aza", -1),
                Arguments.of("section-1-aza", "section-1-aa", -1),
                Arguments.of("section-1-aa", "section-1-ab", -1),
                Arguments.of("section-1-ab", "section-1-b", -1),

                // roman numeral equivalents
                Arguments.of("section-1-a-i", "section-1-a-ia", -1),
                Arguments.of("section-1-a-ia", "section-1-a-ib", -1),
                Arguments.of("section-1-a-ib", "section-1-a-ii", -1),

                // after Z use Z1, Z2, Z3, ...
                Arguments.of("section-360Z", "section-360Z1", -1),
                Arguments.of("section-360Z1", "section-360Z2", -1),
                Arguments.of("section-360Z2", "section-360Z10", -1)
            );
        }

        @ParameterizedTest(name = "[{index}] {0} vs {1} => {2}")
        @MethodSource
        void testCompareSuffix(String s1, String s2, int expectedSign) {
            int result = SectionRangeContainment.compareSuffix(s1, s2);
            assertEquals(expectedSign, Integer.signum(result),
                String.format("Expected compareSuffix('%s', '%s') to have sign %d but got %d",
                    s1, s2, expectedSign, Integer.signum(result)));
        }

        static Stream<Arguments> testCompareSuffix() {
            return Stream.of(
                // basic: empty < A < B < ... < Z
                Arguments.of("", "A", -1),
                Arguments.of("A", "B", -1),
                Arguments.of("Y", "Z", -1),

                // Z-prefix: ZA sorts before A (inserted before first sub-item)
                Arguments.of("ZA", "A", -1),
                Arguments.of("ZB", "A", -1),
                Arguments.of("ZZ", "A", -1),

                // recursive Z-prefix: ZZA before ZA before A
                Arguments.of("ZZA", "ZA", -1),
                Arguments.of("ZZA", "A", -1),

                // sub-insertions: between A and B come AA, AB, ...
                Arguments.of("A", "AA", -1),
                Arguments.of("AA", "AB", -1),
                Arguments.of("AZ", "B", -1),

                // Z-prefix at deeper level: AZA before AA (inserted between A and AA)
                Arguments.of("AZA", "AA", -1),
                Arguments.of("AZB", "AA", -1),

                // Z at end is the letter Z (sorts last among A-Z)
                Arguments.of("Y", "Z", -1),
                Arguments.of("Z", "ZA", 1),  // Z (letter) > ZA (prefix)

                // case insensitive
                Arguments.of("za", "A", -1),
                Arguments.of("a", "B", -1),

                // numeric sub-suffixes (e.g. Z1, Z10)
                Arguments.of("Z9", "Z10", -1),
                Arguments.of("Z2", "Z10", -1),
                Arguments.of("A5", "A10", -1)
            );
        }

        @Test
        @DisplayName("contains with beginning-of-series whole provisions: ZA1 to 1")
        void containsWithBeginningOfSeriesWholeProvisions() {
            assertTrue(contains("schedule-ZA1", "schedule-1", "schedule-ZA1"));
            assertTrue(contains("schedule-ZA1", "schedule-1", "schedule-A1"));
            assertTrue(contains("schedule-ZA1", "schedule-1", "schedule-B1"));
            assertTrue(contains("schedule-ZA1", "schedule-1", "schedule-1"));
            assertFalse(contains("schedule-ZA1", "schedule-1", "schedule-2"));
        }

        @Test
        @DisplayName("contains with inserted provisions: section-1ZA to section-1B")
        void containsWithInsertedProvisions() {
            // 1ZA is inserted before 1A; 1A and 1B are the first two insertions after 1
            assertTrue(contains("section-1ZA", "section-1B", "section-1A"));
            assertTrue(contains("section-1ZA", "section-1B", "section-1AA"));
            assertFalse(contains("section-1ZA", "section-1B", "section-1C"));
        }

        @Test
        @DisplayName("contains with inserted paragraphs before paragraph (a)")
        void containsWithParagraphsBeforeA() {
            assertTrue(contains("section-1-zza", "section-1-a", "section-1-za"));
            assertFalse(contains("section-1-zza", "section-1-a", "section-1-aa"));
        }

        @Test
        @DisplayName("contains with inserted paragraphs: (aza) to (b)")
        void containsWithInsertedParagraphs() {
            // aza is between a and aa; aa is between a and b
            // order: a < aza < aa < ab < ... < az < b
            assertTrue(contains("section-1-aza", "section-1-b", "section-1-aa"));
            assertTrue(contains("section-1-aza", "section-1-b", "section-1-ab"));
            assertFalse(contains("section-1-aza", "section-1-b", "section-1-a"));  // a is before aza
            assertFalse(contains("section-1-aza", "section-1-b", "section-1-c"));
        }

        @Test
        @DisplayName("contains with inserted roman sub-paragraphs: (i) to (ii)")
        void containsWithInsertedRomanSubParagraphs() {
            assertTrue(contains("section-1-a-i", "section-1-a-ii", "section-1-a-ia"));
            assertTrue(contains("section-1-a-i", "section-1-a-ii", "section-1-a-ib"));
            assertFalse(contains("section-1-a-i", "section-1-a-ii", "section-1-a-iii"));
        }

        @Test
        @DisplayName("numeric sub-suffixes: section-360Z1 to section-360Z10")
        void numericSubSuffixes() {
            assertTrue(contains("section-360Z1", "section-360Z10", "section-360Z2"));
            assertTrue(contains("section-360Z1", "section-360Z10", "section-360Z9"));
            assertFalse(contains("section-360Z1", "section-360Z10", "section-360Z11"));
        }

        @Test
        @DisplayName("1Z sorts after 1AA (Z is 26th letter, not a prefix)")
        void zAsLetterSortsAfterAA() {
            // 1Z is the last inserted section before 2
            // 1AA is a sub-insertion after 1A
            // So 1AA < 1AZ < 1B < ... < 1Z
            assertFalse(contains("section-1AA", "section-1AZ", "section-1Z"));
            assertTrue(contains("section-1AA", "section-1Z", "section-1B"));
        }
    }

    @Nested
    @DisplayName("parseRoman: roman numeral validation")
    class ParseRomanTests {

        @Test
        @DisplayName("valid roman numerals parse correctly")
        void validRomanNumerals() {
            assertEquals(1, Roman.parse("i"));
            assertEquals(2, Roman.parse("ii"));
            assertEquals(3, Roman.parse("iii"));
            assertEquals(4, Roman.parse("iv"));
            assertEquals(9, Roman.parse("ix"));
            assertEquals(10, Roman.parse("x"));
            assertEquals(14, Roman.parse("xiv"));
            assertEquals(40, Roman.parse("xl"));
        }

        @Test
        @DisplayName("invalid sequences return 0")
        void invalidSequences() {
            assertEquals(0, Roman.parse("iiii"));  // should be iv
            assertEquals(0, Roman.parse("dd"));    // not valid
            assertEquals(0, Roman.parse("vv"));    // not valid
        }

        @Test
        @DisplayName("non-roman characters return 0")
        void nonRomanCharacters() {
            assertEquals(0, Roman.parse("bc"));
            assertEquals(0, Roman.parse("az"));
        }
    }

    private static boolean contains(String start, String end, String id) {
        return SectionRangeContainment.contains(start, end, id);
    }

}
