package uk.gov.legislation.converters.ld;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LDConverterTest {


    @ParameterizedTest(name = "{index} => uri=''{0}'', expected={1}")
    @MethodSource("validIntegerUris")
    void testExtractIntegerAtEndOfUri_withValidOrInvalidInput(String uriStr, Integer expected) throws URISyntaxException {
        URI uri = uriStr == null ? null : new URI(uriStr);
        assertEquals(expected, LDConverter.extractIntegerAtEndOfUri(uri));
    }

    static Stream<Arguments> validIntegerUris() {
        return Stream.of(
            Arguments.of("http://example.com/path/123", 123),
            Arguments.of(null, null)
        );
    }

    @ParameterizedTest(name = "{index} => uri=''{0}'', expectedMessage=''{1}''")
    @MethodSource("invalidIntegerUris")
    void testExtractIntegerAtEndOfUri_withInvalidComponent(String uriStr, String expectedMessage) throws URISyntaxException {
        URI uri = new URI(uriStr);
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> LDConverter.extractIntegerAtEndOfUri(uri));
        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> invalidIntegerUris() {
        return Stream.of(
            Arguments.of("http://example.com/path/abc", "For input string: \"abc\""),
            Arguments.of("http://example.com/path/", "For input string: \"\"")
        );
    }

    @ParameterizedTest(name = "{index} => uri=''{0}'', expected=''{1}''")
    @MethodSource("extractLastComponentUris")
    void testExtractLastComponentOfUri(String uriStr, String expected) throws URISyntaxException {
        URI uri = uriStr == null ? null : new URI(uriStr);
        assertEquals(expected, LDConverter.extractLastComponentOfUri(uri));
    }

    static Stream<Arguments> extractLastComponentUris() {
        return Stream.of(
            Arguments.of("http://example.com/some/path/com", "com"),
            Arguments.of("http://example.com/some/path/", ""),
            Arguments.of("http://example.com/component", "component"),
            Arguments.of("http://example.com", "example.com"),
            Arguments.of("http://example.com/path/with%20spaces", "with%20spaces"),
            Arguments.of(null, null)
        );
    }

    @ParameterizedTest(name = "{index} => uri=''{0}'', expected={1}")
    @MethodSource("validDateUris")
    void testExtractDateAtEndOfUri_withValidInput(String uriStr, LocalDate expected) throws URISyntaxException {
        URI uri = uriStr == null ? null : new URI(uriStr);
        assertEquals(expected, LDConverter.extractDateAtEndOfUri(uri));
    }

    static Stream<Arguments> validDateUris() {
        return Stream.of(
            Arguments.of("http://example.com/path/2025-08-06", LocalDate.of(2025, 8, 6)),
            Arguments.of(null, null)
        );
    }

    @ParameterizedTest(name = "{index} => uri=''{0}'', expectedMessage=''{1}''")
    @MethodSource("invalidDateUris")
    void testExtractDateAtEndOfUri_withInvalidInput(String uriStr, String expectedMessage) throws URISyntaxException {
        URI uri = new URI(uriStr);
        Exception exception = assertThrows(Exception.class, () -> LDConverter.extractDateAtEndOfUri(uri));
        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream <Arguments> invalidDateUris() {
        return Stream.of(
            Arguments.of("http://example.com/path/invalid-date", "Text 'invalid-date' could not be parsed at index 0"),
            Arguments.of("http://example.com", "Text 'example.com' could not be parsed at index 0"),
            Arguments.of("http://example.com/path/", "Text '' could not be parsed at index 0")
        );
    }
}