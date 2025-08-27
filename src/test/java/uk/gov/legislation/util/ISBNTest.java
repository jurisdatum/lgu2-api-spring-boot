package uk.gov.legislation.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

 class ISBNTest {

    static Stream<Arguments> isbnProvider() {
        return Stream.of(
            Arguments.of("9780306406157", "978-0-306-40615-7", "13-digit ISBN"),
            Arguments.of("0306406152", "0-306-40615-2", "10-digit ISBN with group 0"),
            Arguments.of("4606406152", "4-606-40615-2", "10-digit ISBN with group 4"),
            Arguments.of("8906406152", "8-9064-0615-2", "10-digit ISBN with group 8"),
            Arguments.of("12345", "12345", "Invalid length ISBN"),
            Arguments.of("", "", "Empty string"),
            Arguments.of("978-0306406157", "978-0306406157", "ISBN with special characters")
        );
    }

    @DisplayName("ISBN format tests")
    @ParameterizedTest(name = "{2}")
    @MethodSource("isbnProvider")
     void testISBNFormatting(String input, String expected, String description) {
        assertEquals(expected, ISBN.format(input));
    }

     @SuppressWarnings("DataFlowIssue")
     @Test
     void testFormatNullISBN() {
        assertThrows(NullPointerException.class, () -> ISBN.format(null));
    }
}