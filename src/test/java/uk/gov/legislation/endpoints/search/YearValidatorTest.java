package uk.gov.legislation.endpoints.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.test.LoggingTestWatcher;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(LoggingTestWatcher.class)
 class YearValidatorTest {

    // Valid: Only year is provided
    @Test
    void testOnlyYearProvided() {
        assertDoesNotThrow(() -> SearchController.validateYears(2022, null, null));
    }

    // Valid: Only startYear and endYear provided in valid order
    @Test
    void testValidStartAndEndYear() {
        assertDoesNotThrow(() -> SearchController.validateYears(null, 2000, 2020));
    }

    // Valid: Only startYear provided
    @Test
    void testOnlyStartYear() {
        assertDoesNotThrow(() -> SearchController.validateYears(null, 2000, null));
    }

    // Valid: Only endYear provided
    @Test
    void testOnlyEndYear() {
        assertDoesNotThrow(() -> SearchController.validateYears(null, null, 2020));
    }

    // Invalid: year combined with startYear
    @Test
    void testYearWithStartYearThrows() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> SearchController.validateYears(2022, 2000, null));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("`year` cannot be combined"));
    }

    // Invalid: year combined with endYear
    @Test
    void testYearWithEndYearThrows() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> SearchController.validateYears(2022, null, 2020));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("`year` cannot be combined"));
    }

    // Invalid: startYear > endYear
    @Test
    void testStartYearGreaterThanEndYearThrows() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> SearchController.validateYears(null, 2021, 2020));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("`startYear` must be ≤ `endYear`"));
    }

    // Valid: All inputs null
    @Test
    void testAllNullInputs() {
        assertDoesNotThrow(() -> SearchController.validateYears(null, null, null));
    }
}
