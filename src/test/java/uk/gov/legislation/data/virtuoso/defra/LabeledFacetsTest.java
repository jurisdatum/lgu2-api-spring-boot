package uk.gov.legislation.data.virtuoso.defra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static java.net.URI.create;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LabeledFacetsTest {


    private static final String BASE_WHERE = "?item <http://www.legislation.gov.uk/ukpga> ?x .";
    private static final String PROP = "http://www.legislation.gov.uk/ukpga";

    private record ExpectedCount(String uri, String id, String label, int count) {}

    private record TestCase(String label, String mockJson, List<ExpectedCount> expectedCounts) {}

    static Stream<TestCase> fetchSuccessCases() {
        return Stream.of(
            new TestCase(
                LabeledFacets.RDFS_LABEL,
                """
                    {
                        "results": {
                            "bindings": [
                                {
                                    "x": { "value": "http://www.legislation.gov.uk/uksi" },
                                    "label": { "value": "Label 1" },
                                    "cnt": { "value": "5" }
                                },
                                {
                                    "x": { "value": "http://www.legislation.gov.uk/ukla" },
                                    "label": { "value": "Label 2" },
                                    "cnt": { "value": "10" }
                                }
                            ]
                        }
                    }
                """,
                List.of(
                    new ExpectedCount("http://www.legislation.gov.uk/ukla", "ukla", "Label 2", 10),
                    new ExpectedCount("http://www.legislation.gov.uk/uksi", "uksi", "Label 1", 5)
                )
            ),
            new TestCase(
                LabeledFacets.PREF_LABEL,
                """
                    {
                        "results": {
                            "bindings": [
                                {
                                    "x": { "value": "http://www.legislation.gov.uk/uksi" },
                                    "label": { "value": "CustomLabel 1" },
                                    "cnt": { "value": "3" }
                                },
                                {
                                    "x": { "value": "http://www.legislation.gov.uk/ukpga" },
                                    "label": { "value": "CustomLabel 2" },
                                    "cnt": { "value": "7" }
                                }
                            ]
                        }
                    }
                """,
                List.of(
                    new ExpectedCount("http://www.legislation.gov.uk/ukpga", "ukpga", "CustomLabel 2", 7),
                    new ExpectedCount("http://www.legislation.gov.uk/uksi", "uksi", "CustomLabel 1", 3)
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("fetchSuccessCases")
    void fetch_WithValidResponses_Success(TestCase testCase) {
        var mockDefra = mock(DefraLex.class);
        when(mockDefra.getSparqlResultsJson(anyString()))
            .thenReturn(CompletableFuture.completedFuture(testCase.mockJson()));

        var resultFuture = LabeledFacets.fetch(mockDefra, BASE_WHERE, PROP, testCase.label());
        var result = resultFuture.join();

        assertNotNull(result);
        assertEquals(testCase.expectedCounts().size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            var expected = testCase.expectedCounts().get(i);
            var actual = result.get(i);

            assertAll("Facet " + i,
                () -> assertEquals(create(expected.uri()), actual.uri(), "URI mismatch"),
                () -> assertEquals(expected.id(), actual.id(), "ID mismatch"),
                () -> assertEquals(expected.label(), actual.label(), "Label mismatch"),
                () -> assertEquals(expected.count(), actual.count(), "Count mismatch")
            );
        }
    }

    @Test
    void fetch_HandlesEmptyResponse_Success() {
        var mockDefra = mock(DefraLex.class);
        var mockJsonResponse = """
                {
                    "results": {
                        "bindings": []
                    }
                }
            """;

        when(mockDefra.getSparqlResultsJson(anyString()))
            .thenReturn(CompletableFuture.completedFuture(mockJsonResponse));

        var result = LabeledFacets.fetch(mockDefra, BASE_WHERE, PROP).join();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void fetch_HandlesInvalidJson_ExceptionThrown() {
        var mockDefra = mock(DefraLex.class);
        when(mockDefra.getSparqlResultsJson(anyString()))
            .thenReturn(CompletableFuture.completedFuture("{ invalid json }"));

        var future = LabeledFacets.fetch(mockDefra, BASE_WHERE, PROP);
        assertThrows(CompletionException.class, future::join);
    }

    @Test
    void fetch_HandlesNullResponse_ExceptionThrown() {
        var mockDefra = mock(DefraLex.class);
        when(mockDefra.getSparqlResultsJson(anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        var future = LabeledFacets.fetch(mockDefra, BASE_WHERE, PROP);
        assertThrows(CompletionException.class, future::join);
    }
}