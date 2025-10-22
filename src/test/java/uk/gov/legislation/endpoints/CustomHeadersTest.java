package uk.gov.legislation.endpoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import uk.gov.legislation.data.marklogic.legislation.Legislation.Redirect;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class  CustomHeadersTest {

    @ParameterizedTest(name = "{index} => lang={0}, redirect={1}, expectedVersion={2}")
    @MethodSource("provideHeadersTestCases")
    @DisplayName("Should build headers correctly from language and redirect")
    void testMake(String language, Redirect redirect, String expectedVersion, ExpectedHeaders expected) {
        HttpHeaders headers = CustomHeaders.make(language, redirect);

        assertAll("Custom Headers",
            () -> assertEquals(language, headers.getFirst(HttpHeaders.CONTENT_LANGUAGE)),
            () -> assertEquals(expected.type, headers.getFirst(CustomHeaders.TYPE_HEADER)),
            () -> assertEquals(expected.year, headers.getFirst(CustomHeaders.YEAR_HEADER)),
            () -> assertEquals(expected.number, headers.getFirst(CustomHeaders.NUMBER_HEADER)),
            () -> assertEquals(expectedVersion, headers.getFirst(CustomHeaders.VERSION_HEADER))
        );
    }

    static Stream<Arguments> provideHeadersTestCases() {
        return Stream.of(
            Arguments.of(
                "en",
                new Redirect("ukla", "2025", 12, Optional.of("enacted")),
                "enacted",
                new ExpectedHeaders("ukla", "2025", "12")
            ),
            Arguments.of(
                "fr",
                null,
                null,
                new ExpectedHeaders(null, null, null)
            ),
            Arguments.of(
                null,
                new Redirect("ukpga", "2023", 5, Optional.of("made")),
                "made",
                new ExpectedHeaders("ukpga", "2023", "5")
            ),
            Arguments.of(
                null,
                null,
                null,
                new ExpectedHeaders(null, null, null)
            )
        );
    }

    //  helper class for expected headers
        record ExpectedHeaders(String type, String year, String number) {
    }
}