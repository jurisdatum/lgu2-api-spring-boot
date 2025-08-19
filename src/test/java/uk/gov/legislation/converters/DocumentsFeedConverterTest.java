package uk.gov.legislation.converters;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import uk.gov.legislation.endpoints.search.SearchParameters;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentsFeedConverterTest {

    @Test
    void testConvert_withValidAtomAndQuery() {
        SearchResults searchResults = createSearchResults(5, 50);
        searchResults.facets = createFacetsWithSubjects("B");

        SearchResults.Entry entry = createEntry();
        searchResults.entries = List.of(entry);

        SearchParameters query = createQuery("B");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertAll("Page metadata",
            () -> assertEquals(1, result.meta.page),
            () -> assertEquals(10, result.meta.pageSize),
            () -> assertEquals(5, result.meta.totalPages),
            () -> assertEquals(50, result.meta.counts.total),
            () -> assertEquals(List.of("B"), result.meta.subjects)
        );

        assertAll("Document list",
            () -> assertEquals(1, result.documents.size()),
            () -> assertEquals("1234", result.documents.getFirst().id)
        );
    }

    @Test
    void testConvert_withFilteredSubjects() {
        SearchResults searchResults = createSearchResults(3, 30);
        searchResults.facets = createFacetsWithSubjects("Finance", "Health", "Education");

        SearchParameters query = createQuery("Health");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertEquals(List.of("Health"), result.meta.subjects);
    }

    @Test
    void testConvert_withDocumentInvalidId() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Entry entry = createEntry();
        entry.id = "http://unknown-domain/id/5678";
        searchResults.entries = List.of(entry);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertEquals("http://unknown-domain/id/5678", result.documents.getFirst().id);
    }

    @Test
    void testConvert_withMissingDocumentDetails() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Entry entry = createEntry();
        entry.year = null;
        entry.number = null;
        entry.title = "Test Document";
        searchResults.entries = List.of(entry);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertEquals(0, result.documents.getFirst().year);
        assertNull(result.documents.getFirst().number);
        assertEquals("Test Document", result.documents.getFirst().title);
    }

    @Test
    void testConvert_withAlternativeNumbers() {
        SearchResults searchResults = new SearchResults();
        SearchResults.Entry entry = createEntry();
        searchResults.facets =  new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Entry.AlternativeNumber altNumber1 = new SearchResults.Entry.AlternativeNumber();
        altNumber1.Category = "TypeA";
        altNumber1.Value = "123-A";
        SearchResults.Entry.AlternativeNumber altNumber2 = new SearchResults.Entry.AlternativeNumber();
        altNumber2.Category = "TypeB";
        altNumber2.Value = "456-B";
        entry.altNumbers = List.of(altNumber1, altNumber2);
        searchResults.entries = List.of(entry);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        var document = result.documents.getFirst();
        var altNumbers = document.altNumbers;

        assertAll("Alt Numbers assertions",
            () -> assertEquals(2, altNumbers.size()),

            () -> assertEquals("123-A", altNumbers.getFirst().value),
            () -> assertEquals("TypeA", altNumbers.getFirst().category),

            () -> assertEquals("456-B", altNumbers.get(1).value),
            () -> assertEquals("TypeB", altNumbers.get(1).category)
        );
    }

    @Test
    void testConvert_withInvalidLinkFormats() {
        SearchResults searchResults = new SearchResults();
        SearchResults.Entry entry = createEntry();
        searchResults.facets =  new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Link link = new SearchResults.Link();
        link.rel = "alternate";
        link.type = "application/invalid-type";
        entry.links = List.of(link);
        searchResults.entries = List.of(entry);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertTrue(result.documents.getFirst().formats.isEmpty());
    }

    @Test
    void testConvert_withNullSubjects() {
        SearchResults searchResults = createSearchResults(5, 50);
        searchResults.facets = new SearchResults.Facets(); // No subjects

        SearchParameters query = createQuery("A");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertAll("Page metadata",
            () -> assertEquals(1, result.meta.page),
            () -> assertEquals(10, result.meta.pageSize),
            () -> assertEquals(5, result.meta.totalPages),
            () -> assertEquals(50, result.meta.counts.total),
            () -> assertTrue(result.meta.subjects.isEmpty(), "Subjects list should be empty")
        );
    }

    @Test
    void testConvert_withEmptyEntries() {
        SearchResults searchResults = new SearchResults();
        searchResults.entries = Collections.emptyList();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertTrue(result.documents.isEmpty());
    }

    @Test
    void testConvert_withFilterOnSubjects() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = createFacetsWithSubjects("A", "B");
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        SearchParameters query = createQuery("B");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertEquals(List.of("B"), result.meta.subjects);
    }

    @Test
    void testConvert_withNullEntries() {
        SearchResults searchResults = new SearchResults();
        searchResults.entries = null;
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertTrue(result.documents.isEmpty());
    }

    @Test
    void testConvert_withNonMatchingSubjects() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = createFacetsWithSubjects("X", "Y");
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        SearchParameters query = createQuery("Z");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertTrue(result.meta.subjects.isEmpty());
    }

    @Test
    void testConvert_withPartialSubjectMatch() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = createFacetsWithSubjects("Finance", "Health", "Education");
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        SearchParameters query = createQuery("F");

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, query);

        assertEquals(List.of("Finance"), result.meta.subjects);
    }

    @Test
    void testConvert_withEmptySearchResults() {
        SearchResults searchResults = new SearchResults();
        searchResults.entries = Collections.emptyList();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertEquals(0, result.documents.size());
        assertNotNull(result.meta);
        assertEquals(0, result.meta.counts.total);
    }

    @Test
    void testConvert_withAlternateDocumentIdFormats() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Entry entry1 = createEntry();
        entry1.id = "http://www.legislation.gov.uk/id/5678";
        SearchResults.Entry entry2 = createEntry();
        entry2.id = "http://www.legislation.gov.uk/5678";
        SearchResults.Entry entry3 = createEntry();
        entry3.id = "http://custom-domain/id/5678";
        searchResults.entries = List.of(entry1, entry2, entry3);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertAll("Documents list",
            () -> assertEquals(3, result.documents.size()),
            () -> assertEquals("5678", result.documents.getFirst().id),
            () -> assertEquals("5678", result.documents.get(1).id),
            () -> assertEquals("http://custom-domain/id/5678", result.documents.get(2).id)
        );
    }

    @Test
    void testConvert_withNullFacets() {
        SearchResults searchResults = createSearchResults(2, 20);
        searchResults.facets = new SearchResults.Facets();

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertAll("Page metadata",
            () -> assertEquals(1, result.meta.page),
            () -> assertEquals(10, result.meta.pageSize),
            () -> assertEquals(2, result.meta.totalPages),
            () -> assertEquals(20, result.meta.counts.total),
            () -> assertTrue(result.meta.subjects.isEmpty(), "Expected subjects to be empty")
        );
    }


    @Test
    void testConvert_withNoSubjectsInInputAndQuery() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        searchResults.facets.subjects = null;
        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertNotNull(result.meta);
        assertTrue(result.meta.subjects.isEmpty());
    }


    @Test
    void testConvert_withMatchingAltNumbersInDocuments() {
        SearchResults searchResults = new SearchResults();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        SearchResults.Entry entry1 = createEntry();
        SearchResults.Entry.AlternativeNumber altNumber = new SearchResults.Entry.AlternativeNumber();
        altNumber.Category = "AltType";
        altNumber.Value = "999-XX";
        entry1.altNumbers = List.of(altNumber);

        searchResults.entries = List.of(entry1);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        var document = result.documents.getFirst();
        var altNumber1 = document.altNumbers.getFirst();

        assertAll("Document and AltNumber assertions",
            () -> assertEquals(1, result.documents.size(), "Expected exactly 1 document"),
            () -> assertEquals(1, document.altNumbers.size(), "Expected exactly 1 alt number"),
            () -> assertEquals("AltType", altNumber1.category, "Alt number category mismatch"),
            () -> assertEquals("999-XX", altNumber1.value, "Alt number value mismatch")
        );
    }

    @Test
    void testConvert_withInvalidDocumentType() {
        SearchResults searchResults = new SearchResults();
        SearchResults.Entry entry1 = createEntry();
        searchResults.facets = new SearchResults.Facets();
        searchResults.facets.facetYears = new SearchResults.FacetYears();
        entry1.mainType = null;

        searchResults.entries = List.of(entry1);

        PageOfDocuments result = DocumentsFeedConverter.convert(searchResults, null);

        assertEquals(1, result.documents.size());
        assertNull(result.documents.getFirst().longType);
    }


    // Helper Methods

    private SearchResults createSearchResults(int morePages, int totalResults) {
        SearchResults sr = new SearchResults();
        sr.page = 1;
        sr.itemsPerPage = 10;
        sr.morePages = morePages;
        sr.totalResults = totalResults;
        sr.updated = ZonedDateTime.now();
        return sr;
    }

    private SearchParameters createQuery(String subject) {
        SearchParameters query = new SearchParameters();
        query.setSubject(subject);
        return query;
    }

    private SearchResults.Entry createEntry() {
        SearchResults.Entry entry = new SearchResults.Entry();
        entry.id = "http://www.legislation.gov.uk/id/1234";
        entry.links = new ArrayList<>();
        return entry;
    }

    private SearchResults.Facets createFacetsWithSubjects(String... names) {
        SearchResults.Facets facets = new SearchResults.Facets();
        SearchResults.Subjects subjects = new SearchResults.Subjects();
        subjects.headings = Arrays.stream(names)
            .map(this::createSubjectHeading)
            .toList();
        facets.subjects = subjects;
        return facets;
    }

    private SearchResults.SubjectHeading createSubjectHeading(String name) {
        SearchResults.SubjectHeading heading = new SearchResults.SubjectHeading();
        heading.name = name;
        return heading;
    }
}