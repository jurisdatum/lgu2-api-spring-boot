package uk.gov.legislation.converters.ld;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.api.responses.ld.Reign;
import uk.gov.legislation.data.virtuoso.jsonld.ReignLD;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ReignConverterTest {

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideReignConversionCases")
    void testConvert_ReignLD(String testCase, ReignLD input, Reign expected, Class<? extends Throwable> expectedException) {
        if (expectedException != null) {
            assertThrows(expectedException, () -> ReignConverter.convert(input), testCase);
            return;
        }

        Reign actual = ReignConverter.convert(input);
        assertAll(testCase,
            () -> assertEquals(expected.uri, actual.uri, "URI mismatch"),
            () -> assertEquals(expected.label, actual.label, "Label mismatch"),
            () -> assertEquals(expected.monarchs, actual.monarchs, "Monarchs mismatch"),
            () -> assertEquals(expected.startDate, actual.startDate, "Start date mismatch"),
            () -> assertEquals(expected.endDate, actual.endDate, "End date mismatch"),
            () -> assertEquals(expected.startRegnalYear, actual.startRegnalYear, "Start regnal year mismatch"),
            () -> assertEquals(expected.endRegnalYear, actual.endRegnalYear, "End regnal year mismatch")
        );
    }

    static Stream<Arguments> provideReignConversionCases() {
        URI id = URI.create("http://example.org/reign/1");
        URI monarch1 = URI.create("http://example.org/monarch/king-john");
        URI monarch2 = URI.create("http://example.org/monarch/queen-anne");
        URI startDate = URI.create("http://example.org/startDate/2020-01-01");
        URI endDate = URI.create("http://example.org/endDate/2024-12-31");
        URI regnalStart = URI.create("http://example.org/startRegnalYear/1");
        URI regnalEnd = URI.create("http://example.org/endRegnalYear/10");

        return Stream.of(
            Arguments.of(
                "All fields populated",
                newReignLD(id, "The Great Reign", List.of(monarch1, monarch2), startDate, endDate, regnalStart, regnalEnd),
                buildExpected(id, "The Great Reign", List.of("king-john", "queen-anne"),
                    LocalDate.of(2020, 1, 1), LocalDate.of(2024, 12, 31), 1, 10),
                null
            ),
            Arguments.of("Null input", null, null, NullPointerException.class),
            Arguments.of(
                "Null monarch list",
                newReignLD(id, "Label", null, startDate, endDate, regnalStart, regnalEnd),
                null, NullPointerException.class
            ),
            Arguments.of(
                "Empty monarch list",
                newReignLD(id, "Label", List.of(), startDate, endDate, regnalStart, regnalEnd),
                buildExpected(id, "Label", List.of(), LocalDate.of(2020, 1, 1), LocalDate.of(2024, 12, 31), 1, 10),
                null
            ),
            Arguments.of(
                "Only ID and Label",
                newReignLD(id, "Label", List.of(), null, null, null, null),
                buildExpected(id, "Label", List.of(), null, null, null, null),
                null
            ),
            Arguments.of(
                "All fields null",
                newReignLD(null, null, null, null, null, null, null),
                null, NullPointerException.class
            )
        );
    }

    private static Reign buildExpected(URI uri, String label, List<String> monarchs,
        LocalDate start, LocalDate end,
        Integer startRegnal, Integer endRegnal) {
        Reign r = new Reign();
        r.uri = uri;
        r.label = label;
        r.monarchs = monarchs;
        r.startDate = start;
        r.endDate = end;
        r.startRegnalYear = startRegnal;
        r.endRegnalYear = endRegnal;
        return r;
    }

    private static ReignLD newReignLD(URI id, String label, List<URI> monarchs,
        URI startDate, URI endDate,
        URI startRegnalYear, URI endRegnalYear) {
        ReignLD ld = new ReignLD();
        ld.id = id;
        ld.label = label;
        ld.monarch = monarchs;
        ld.startDate = startDate;
        ld.endDate = endDate;
        ld.startRegnalYear = startRegnalYear;
        ld.endRegnalYear = endRegnalYear;
        return ld;
    }}


