package uk.gov.legislation.data.virtuoso.defra;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class BooleanFacetsTest {

    /**
     * Tests for the fetch method in the BooleanFacets class.
     * <p>
     * The fetch method is a static method that constructs a SPARQL query,
     * retrieves JSON results asynchronously from a DefraLex instance,
     * and parses them into a list of BooleanFacets.Count records.
     */

    @Test
    void testFetchWithValidResponse() throws Exception {

        String baseWhere = "BASE_WHERE_CONDITION";
        String prop = "https://www.legislation.gov.uk/uksi/2001/1/metadata";
        String expectedQuery = """
                SELECT ?value (COUNT(DISTINCT ?item) AS ?cnt)
                WHERE { BASE_WHERE_CONDITION ?item <https://www.legislation.gov.uk/uksi/2001/1/metadata> ?value . }
                GROUP BY ?value
                ORDER BY DESC(?cnt)
            """;

        String jsonResponse = """
                {
                    "results": {
                        "bindings": [
                            {"value": {"value": "1"}, "cnt": {"value": "10"}},
                            {"value": {"value": "0"}, "cnt": {"value": "5"}}
                        ]
                    }
                }
            """;

        DefraLex mockDefraLex = Mockito.mock(DefraLex.class);
        Mockito.when(mockDefraLex.getSparqlResultsJson(anyString())).thenReturn(CompletableFuture.completedFuture(jsonResponse));

        CompletableFuture <List <BooleanFacets.Count>> resultFuture = BooleanFacets.fetch(mockDefraLex, baseWhere, prop);
        List <BooleanFacets.Count> result = resultFuture.get();

        Mockito.verify(mockDefraLex).getSparqlResultsJson(expectedQuery);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BooleanFacets.Count(true, 10), result.get(0));
        assertEquals(new BooleanFacets.Count(false, 5), result.get(1));
    }

    @Test
    void testFetchWithEmptyResponse() throws Exception {

        String baseWhere = "BASE_WHERE_CONDITION";
        String prop = "https://www.legislation.gov.uk/uksi/2001/1/metadata";
        String jsonResponse = """
                {
                    "results": {
                        "bindings": []
                    }
                }
            """;

        DefraLex mockDefraLex = Mockito.mock(DefraLex.class);
        Mockito.when(mockDefraLex.getSparqlResultsJson(anyString())).thenReturn(CompletableFuture.completedFuture(jsonResponse));

        CompletableFuture <List <BooleanFacets.Count>> resultFuture = BooleanFacets.fetch(mockDefraLex, baseWhere, prop);
        List <BooleanFacets.Count> result = resultFuture.get();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchWithInvalidJsonResponse() {

        String baseWhere = "BASE_WHERE_CONDITION";
        String prop = "https://www.legislation.gov.uk/uksi/2001/1/metadata";
        String invalidJsonResponse = "INVALID_JSON";

        DefraLex mockDefraLex = Mockito.mock(DefraLex.class);
        Mockito.when(mockDefraLex.getSparqlResultsJson(anyString())).thenReturn(CompletableFuture.completedFuture(invalidJsonResponse));

        CompletableFuture <List <BooleanFacets.Count>> resultFuture = BooleanFacets.fetch(mockDefraLex, baseWhere, prop);
        assertThrows(Exception.class, resultFuture::get);
    }

    @Test
    void testFetchWithMalformedBindings() {

        String baseWhere = "BASE_WHERE_CONDITION";
        String prop = "https://www.legislation.gov.uk/uksi/2001/1/metadata";
        String jsonResponse = """
                {
                    "results": {
                        "bindings": [
                            {"value": {"value": "1"}}
                        ]
                    }
                }
            """;

        DefraLex mockDefraLex = Mockito.mock(DefraLex.class);
        Mockito.when(mockDefraLex.getSparqlResultsJson(anyString())).thenReturn(CompletableFuture.completedFuture(jsonResponse));

        CompletableFuture <List <BooleanFacets.Count>> resultFuture = BooleanFacets.fetch(mockDefraLex, baseWhere, prop);
        assertThrows(Exception.class, resultFuture::get);
    }
}