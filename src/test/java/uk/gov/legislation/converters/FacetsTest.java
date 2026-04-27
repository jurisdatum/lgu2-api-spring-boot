package uk.gov.legislation.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FacetsTest {


    @ParameterizedTest
    @MethodSource("provideNullAndEmptyFacetTypes")
    @DisplayName("Should return empty list for null or empty facet types")
    void testConvertTypeFacets_EmptyOrNullCases(String caseName, SearchResults.FacetTypes input) {
        List<PageOfDocuments.ByType> result = Facets.convertTypeFacets(input);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected empty result for case: " + caseName);
    }

    static Stream<Arguments> provideNullAndEmptyFacetTypes() {
        SearchResults.FacetTypes withNullEntries = new SearchResults.FacetTypes();
        withNullEntries.entries = null;

        SearchResults.FacetTypes withEmptyEntries = new SearchResults.FacetTypes();
        withEmptyEntries.entries = Collections.emptyList();

        return Stream.of(
            Arguments.of("Null facetTypes", null),
            Arguments.of("Null entries", withNullEntries),
            Arguments.of("Empty entries", withEmptyEntries)
        );
    }

    @Test
    @DisplayName("Should convert valid entries correctly")
    void testConvertTypeFacets_ValidEntries_ReturnsConvertedList() {
        SearchResults.FacetType entry1 = new SearchResults.FacetType();
        entry1.type = "Type1";
        entry1.value = 5;

        SearchResults.FacetType entry2 = new SearchResults.FacetType();
        entry2.type = "Type2";
        entry2.value = 10;

        SearchResults.FacetTypes facetTypes = new SearchResults.FacetTypes();
        facetTypes.entries = List.of(entry1, entry2);

        List<PageOfDocuments.ByType> result = Facets.convertTypeFacets(facetTypes);

        assertAll("Type facets result",
            () -> assertNotNull(result, "Result should not be null"),
            () -> assertEquals(2, result.size(), "Expected exactly 2 type facets"),

            () -> assertEquals("Type1", result.getFirst().type, "First type mismatch"),
            () -> assertEquals(5, result.getFirst().count, "First type count mismatch"),

            () -> assertEquals("Type2", result.get(1).type, "Second type mismatch"),
            () -> assertEquals(10, result.get(1).count, "Second type count mismatch")
        );
    }

    @Test
    @DisplayName("Should split ukamended qualifier out of the type string")
    void testConvertTypeFacets_UkAmendedQualifier_SplitIntoSeparateField() {
        SearchResults.FacetType plain = new SearchResults.FacetType();
        plain.type = "EuropeanUnionRegulation";
        plain.value = 124855;

        SearchResults.FacetType amended = new SearchResults.FacetType();
        amended.type = "EuropeanUnionRegulation|ukamended=true";
        amended.value = 3683;

        SearchResults.FacetType notAmended = new SearchResults.FacetType();
        notAmended.type = "EuropeanUnionRegulation|ukamended=false";
        notAmended.value = 121172;

        SearchResults.FacetTypes facetTypes = new SearchResults.FacetTypes();
        facetTypes.entries = List.of(plain, amended, notAmended);

        List<PageOfDocuments.ByType> result = Facets.convertTypeFacets(facetTypes);

        assertAll("ukamended split",
            () -> assertEquals(3, result.size()),

            () -> assertEquals("EuropeanUnionRegulation", result.get(0).type),
            () -> assertNull(result.get(0).ukAmended),
            () -> assertEquals(124855, result.get(0).count),

            () -> assertEquals("EuropeanUnionRegulation", result.get(1).type),
            () -> assertEquals(Boolean.TRUE, result.get(1).ukAmended),
            () -> assertEquals(3683, result.get(1).count),

            () -> assertEquals("EuropeanUnionRegulation", result.get(2).type),
            () -> assertEquals(Boolean.FALSE, result.get(2).ukAmended),
            () -> assertEquals(121172, result.get(2).count)
        );
    }

    @Test
    @DisplayName("Should exclude filtered type 'UnitedKingdomDraftPublicBill'")
    void testConvertTypeFacets_FilteredType_ExcludesFilteredEntries() {
        SearchResults.FacetType entry1 = new SearchResults.FacetType();
        entry1.type = "Type1";
        entry1.value = 5;

        SearchResults.FacetType entry2 = new SearchResults.FacetType();
        entry2.type = "UnitedKingdomDraftPublicBill";
        entry2.value = 10;

        SearchResults.FacetType entry3 = new SearchResults.FacetType();
        entry3.type = "Type2";
        entry3.value = 15;

        SearchResults.FacetTypes facetTypes = new SearchResults.FacetTypes();
        facetTypes.entries = List.of(entry1, entry2, entry3);

        List<PageOfDocuments.ByType> result = Facets.convertTypeFacets(facetTypes);

        assertAll("ByType facet results",
            () -> assertNotNull(result, "Result should not be null"),
            () -> assertEquals(2, result.size(), "Expected 2 type facets"),

            () -> assertEquals("Type1", result.getFirst().type, "Type1 name mismatch"),
            () -> assertEquals(5, result.getFirst().count, "Type1 count mismatch"),

            () -> assertEquals("Type2", result.get(1).type, "Type2 name mismatch"),
            () -> assertEquals(15, result.get(1).count, "Type2 count mismatch")
        );
    }
}