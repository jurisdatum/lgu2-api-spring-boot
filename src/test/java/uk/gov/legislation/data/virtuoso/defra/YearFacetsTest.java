package uk.gov.legislation.data.virtuoso.defra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class YearFacetsTest {


    private static final String BASE_WHERE = "?item <http://example.org/property> ?value.";
    private static final String EXPECTED_QUERY = String.format(
        DefraLex.FACET_QUERY,
        "?year",
        BASE_WHERE + " ?item <http://www.legislation.gov.uk/def/legislation/year> ?year .",
        "?year"
    );

    private record CountExpectation(int year, int count) {}
    private record ValidTestCase(String name, String jsonResponse, List<CountExpectation> expectedCounts) {}

    static Stream <ValidTestCase> validCases() {
       return Stream.of(
           new ValidTestCase(
               "valid data",
               """
                   {
                       "results": {
                           "bindings": [
                               {
                                   "year": { "value": "2021" },
                                   "cnt": { "value": "15" }
                               },
                               {
                                   "year": { "value": "2020" },
                                   "cnt": { "value": "10" }
                               }
                           ]
                       }
                   }
               """,
               List.of(
                   new CountExpectation(2021, 15),
                   new CountExpectation(2020, 10)
               )
           ),
           new ValidTestCase(
               "empty result",
               """
                   {
                       "results": {
                           "bindings": []
                       }
                   }
               """,
               List.of()
           ),
           // Test case to validate year-descending sort behavior.
           // 2023 should come before 2019 even though the input has 2019 first.
           // This proves results are sorted by year descending, not input order.
           new ValidTestCase(
               "out of order years",
               """
                   {
                       "results": {
                           "bindings": [
                               {
                                   "year": { "value": "2019" },
                                   "cnt": { "value": "8" }
                               },
                               {
                                   "year": { "value": "2023" },
                                   "cnt": { "value": "12" }
                               }
                           ]
                       }
                   }
               """,
               List.of(
                   new CountExpectation(2023, 12),
                   new CountExpectation(2019, 8)
               )
           )
       );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validCases")
    void fetch_ReturnsExpectedCounts(ValidTestCase testCase) {
       var defraMock = mock(DefraLex.class);
       when(defraMock.getSparqlResultsJson(EXPECTED_QUERY))
           .thenReturn(CompletableFuture.completedFuture(testCase.jsonResponse()));

       var result = YearFacets.fetch(defraMock, BASE_WHERE).join();

       assertNotNull(result);
       assertEquals(testCase.expectedCounts().size(), result.size());

       for (int i = 0; i < testCase.expectedCounts().size(); i++) {
          var expected = testCase.expectedCounts().get(i);
          var actual = result.get(i);
          assertEquals(expected.year(), actual.year());
          assertEquals(expected.count(), actual.count());
       }
    }

    @Test
    void fetch_ThrowsOnInvalidJson() {
       var defraMock = mock(DefraLex.class);
       when(defraMock.getSparqlResultsJson(EXPECTED_QUERY))
           .thenReturn(CompletableFuture.completedFuture("{ invalid json }"));

       var resultFuture = YearFacets.fetch(defraMock, BASE_WHERE);
       assertThrows(CompletionException.class, resultFuture::join);
    }
}