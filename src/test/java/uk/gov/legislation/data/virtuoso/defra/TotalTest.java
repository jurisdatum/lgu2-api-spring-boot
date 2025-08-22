package uk.gov.legislation.data.virtuoso.defra;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TotalTest {



    private static final String QUERY_CONDITION = "?item a :SomeType";
    private static final String EXPECTED_QUERY = "SELECT (COUNT(DISTINCT ?item) AS ?cnt) WHERE { ?item a :SomeType }";

    @Test
    void get_ReturnsParsedResultSuccessfully() {
        var defra = mock(DefraLex.class);
        var jsonResponse = """
            {
                "results": {
                    "bindings": [
                        {
                            "cnt": { "type": "literal", "value": "42" }
                        }
                    ]
                }
            }
        """;

        when(defra.getSparqlResultsJson(anyString()))
            .thenReturn(CompletableFuture.completedFuture(jsonResponse));

        var result = Total.get(defra, QUERY_CONDITION).join();

        assertEquals(42, result);
        verify(defra).getSparqlResultsJson(EXPECTED_QUERY);
    }

    private record ExceptionTestCase(String name, CompletableFuture<String> response) {}

    static Stream<ExceptionTestCase> get_ExceptionCases() {
        var malformedJson = """
            {
                "results": {
                    "binding
                }
            }
        """;

        var malformedJsonFuture = CompletableFuture.completedFuture(malformedJson);

        var failedFuture = new CompletableFuture<String>();
        failedFuture.completeExceptionally(new RuntimeException("Query execution failed"));

        return Stream.of(
            new ExceptionTestCase("malformed json", malformedJsonFuture),
            new ExceptionTestCase("query failure", failedFuture)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("get_ExceptionCases")
    void get_ThrowsException(ExceptionTestCase testCase) {
        var defra = mock(DefraLex.class);
        when(defra.getSparqlResultsJson(anyString()))
            .thenReturn(testCase.response());

        var resultFuture = Total.get(defra, QUERY_CONDITION);

        assertThrows(CompletionException.class, resultFuture::join);
        verify(defra).getSparqlResultsJson(EXPECTED_QUERY);
    }
}