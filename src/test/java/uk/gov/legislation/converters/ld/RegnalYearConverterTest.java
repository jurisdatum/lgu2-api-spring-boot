package uk.gov.legislation.converters.ld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import uk.gov.legislation.api.responses.ld.RegnalYear;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalYearLD;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegnalYearConverterTest {

    @ParameterizedTest
    @NullSource
    @DisplayName("Should return null when input is null")
    void testConvert_NullInput(RegnalYearLD input) {
        RegnalYear result = RegnalYearConverter.convert(input);
        assertNull(result);
    }

    @Test
    @DisplayName("Should correctly convert RegnalYearLD to RegnalYear with all fields populated")
    void testConvert_ValidInput() {

        RegnalYearLD regnalYearLD = new RegnalYearLD();
        regnalYearLD.id = URI.create("http://example.com/regnalYear/1");
        regnalYearLD.label = "1st Year of Reign";
        regnalYearLD.yearOfReign = 1;
        regnalYearLD.reign = URI.create("http://example.com/reign/monarch1");
        regnalYearLD.startDate = URI.create("http://example.com/date/1850-01-01");
        regnalYearLD.endDate = URI.create("http://example.com/date/1850-12-31");

        try (var mockedStatic = mockStatic(LDConverter.class)) {
            // Mock static methods
            mockedStatic.when(() -> LDConverter.extractLastComponentOfUri(regnalYearLD.reign))
                .thenReturn("monarch1");
            mockedStatic.when(() -> LDConverter.extractDateAtEndOfUri(regnalYearLD.startDate))
                .thenReturn(LocalDate.of(1850, 1, 1));
            mockedStatic.when(() -> LDConverter.extractDateAtEndOfUri(regnalYearLD.endDate))
                .thenReturn(LocalDate.of(1850, 12, 31));

            RegnalYear result = RegnalYearConverter.convert(regnalYearLD);

            assertAll("RegnalYear conversion result",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(regnalYearLD.id, result.uri, "URI mismatch"),
                () -> assertEquals(regnalYearLD.label, result.label, "Label mismatch"),
                () -> assertEquals(regnalYearLD.yearOfReign, result.yearOfReign, "Year of reign mismatch"),
                () -> assertEquals("monarch1", result.reign, "Reign mismatch"),
                () -> assertEquals(LocalDate.of(1850, 1, 1), result.startDate, "Start date mismatch"),
                () -> assertEquals(LocalDate.of(1850, 12, 31), result.endDate, "End date mismatch")
            );

        }
    }


}