package uk.gov.legislation.converters.ld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.api.responses.ld.Monarch;
import uk.gov.legislation.data.virtuoso.jsonld.MonarchLD;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class MonarchConverterTest {


    @ParameterizedTest
    @MethodSource("provideMonarchLDInputs")
    @DisplayName("Test MonarchConverter.convert with various MonarchLD input combinations")
    void testConvert(MonarchLD input, URI expectedUri, String expectedLabel, String expectedName, Integer expectedNumber) {
        Monarch result = MonarchConverter.convert(input);

        assertEquals(expectedUri, result.uri);
        assertEquals(expectedLabel, result.label);
        assertEquals(expectedName, result.name);
        assertEquals(expectedNumber, result.number);
    }

    static Stream <Arguments> provideMonarchLDInputs() {
        return Stream.of(
            // Case: All fields populated
            Arguments.of(
                newMonarchLD(
                    URI.create("http://example.com/monarch/1"),
                    "Example Monarch",
                    "King Example",
                    1
                ),
                URI.create("http://example.com/monarch/1"),
                "Example Monarch",
                "King Example",
                1
            ),
            // Case: Optional fields missing
            Arguments.of(
                newMonarchLD(
                    URI.create("http://example.com/monarch/2"),
                    null,
                    null,
                    null
                ),
                URI.create("http://example.com/monarch/2"),
                null,
                null,
                null
            ),
            // Case: Empty strings and nulls
            Arguments.of(
                newMonarchLD(
                    null,
                    "",
                    "",
                    null
                ),
                null,
                "",
                "",
                null
            )
        );
    }

    // Helper method to create MonarchLD instances
    private static MonarchLD newMonarchLD(URI id, String label, String regnalName, Integer regnalNumber) {
        MonarchLD monarchLD = new MonarchLD();
        monarchLD.id = id;
        monarchLD.label = label;
        monarchLD.regnalName = regnalName;
        monarchLD.regnalNumber = regnalNumber;
        return monarchLD;
    }
}