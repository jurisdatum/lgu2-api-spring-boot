package uk.gov.legislation.endpoints.documents;

import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.Links;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 A class to convert SearchResults into DocumentList objects.
 */
@Component
public class Converter {


    /**
     * Converts a SearchResults object into a DocumentList.
     *
     * @param results the search results to be converted
     * @return a DocumentList containing metadata and document details
     */

    public static DocumentList convert(SearchResults results) {
        Meta meta = convertMeta(results);
        List<Document> documents = convertDocuments(results);
        return new Response(meta, documents);
    }

    /**
     * Converts the metadata of SearchResults into a Meta object.
     *
     * @param results the search results containing metadata
     * @return a Meta object
     */
    private static Meta convertMeta(SearchResults results) {
        Counts counts = convertCounts(results);
        SortedSet<String> subjects = extractSubjectHeadings(results.facets.subjects);
        return new Meta(
                results.page,
                results.itemsPerPage,
                results.morePages,
                results.updated,
                counts,
                subjects
        );
    }

    /**
     * Converts the counts from SearchResults into a Counts object.
     *
     * @param results the search results containing counts
     * @return a Counts object
     */
    private static Counts convertCounts(SearchResults results) {
        int total = results.facets.facetYears.entries.stream()
                .mapToInt(facet -> facet.total)
                .sum();
        List<ByType> byType = convertTypeFacets(results.facets.facetTypes);
        List<ByYear> byYear = convertYearFacets(results.facets.facetYears);
        List<ByInitial> bySubjectInitial = convertSubjectFacets(results.facets.subjects);
        return new Counts(total, byType, byYear, bySubjectInitial);
    }

    /**
     * Converts type facets from SearchResults into a list of ByType objects.
     *
     * @param facetTypes the facet types from search results
     * @return a list of ByType objects
     */
    private static List<ByType> convertTypeFacets(SearchResults.FacetTypes facetTypes) {
        if (facetTypes == null || facetTypes.entries == null) {
            return Collections.emptyList();
        }
        return facetTypes.entries.stream()
                .filter(facet -> !facet.type.equals("UnitedKingdomDraftPublicBill"))
                .map(facet -> new ByType(facet.type, facet.value))
                .toList();
    }

    /**
     * Converts year facets from SearchResults into a list of ByYear objects.
     *
     * @param facetYears the year facets from search results
     * @return a list of ByYear objects
     */

    private static List<ByYear> convertYearFacets(SearchResults.FacetYears facetYears) {
        if (facetYears == null || facetYears.entries == null) {
            return Collections.emptyList();
        }
        return facetYears.entries.stream()
                .map(facet -> new ByYear(facet.year, facet.total))
                .toList();
    }

    /**
     * Converts subject facets from SearchResults into a list of ByInitial objects.
     *
     * @param subjects the subject facets from search results
     * @return a list of ByInitial objects
     */

    private static List<ByInitial> convertSubjectFacets(SearchResults.Subjects subjects) {
        if (subjects == null || subjects.initials == null) {
            return Collections.emptyList();
        }
        return subjects.initials.stream()
                .map(subject -> new ByInitial(subject.initial, subject.total))
                .toList();
    }

    /**
     * Retrieves subject headings as a sorted set from SearchResults.
     *
     * @param subjects the subject headings from search results
     * @return a sorted set of subject headings
     */

    private static SortedSet<String> extractSubjectHeadings(SearchResults.Subjects subjects) {
        if (subjects == null || subjects.headings == null) {
            return Collections.emptySortedSet();
        }
        return subjects.headings.stream()
                .map(heading -> heading.name)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Converts document entries from SearchResults into a list of Document objects.
     *
     * @param results the search results containing document entries
     * @return a list of Document objects
     */
    private static List<Document> convertDocuments(SearchResults results) {
        if (results.entries == null) {
            return Collections.emptyList();
        }
        return results.entries.stream()
                .map(Converter::convertDocument)
                .toList();
    }

    /**
     * Converts a single document entry into a Document object.
     *
     * @param entry the document entry to be converted
     * @return a Document object
     */

    private static Document convertDocument(SearchResults.Entry entry) {
        String id = Optional.ofNullable(entry.id)
                .filter(e -> e.length() > 33)
                .map(e -> e.substring(33))
                .orElse(null);

        String longType = Optional.ofNullable(entry.mainType).map(mt -> mt.value).orElse(null);
        int year = Optional.ofNullable(entry.year).map(yr -> yr.value).orElse(0);
        int number = Optional.ofNullable(entry.number).map(num -> num.value).orElse(0);

        List<AltNumber> altNumbers = Optional.ofNullable(entry.altNumbers)
                .map(alts -> alts.stream()
                        .map(alt -> new AltNumber(alt.Category, alt.Value))
                        .toList())
                .orElse(Collections.emptyList());

        String cite = Cites.make(longType, year, number, altNumbers);

        String version = extractVersion(entry.links);
        List<String> formats = extractFormats(entry.links);

        return new Document(
                id,
                entry.title,
                entry.altTitle,
                longType,
                year,
                number,
                altNumbers,
                cite,
                entry.published,
                entry.updated,
                version,
                formats
        );
    }

    /**
     * Extracts the document version from the Atom links.
     *
     * @param links the list of links to search for the version
     * @return the document version, or null if not found
     */

    private static String extractVersion(List<SearchResults.Link> links) {
        return links.stream()
            .filter(link -> link.rel == null)
            .findFirst()
            .map(link -> link.href)
            .map(Links::parse)
            .flatMap(Links.Components::version)
            .orElse(null);
    }

    /**
     * Extracts supported formats from a list of links.
     *
     * @param links the list of links to search for formats
     * @return a list of format strings (e.g., "xml", "pdf")
     */

    private static List<String> extractFormats(List<SearchResults.Link> links) {
        return links.stream()
                .filter(link -> "alternate".equals(link.rel))
                .map(link -> link.type)
                .filter(type -> "application/xml".equals(type) || "application/pdf".equals(type))
                .map(type -> type.substring(12))
                .toList();
    }

    /**
     * Inner record classes
     */
    private record Response(Meta meta, List<? extends DocumentList.Document> documents) implements DocumentList { }

    private record Meta(int page, int pageSize, int totalPages, LocalDateTime updated, Counts counts, SortedSet<String> subjects)
            implements DocumentList.Meta { }

    private record Counts(int total, List<ByType> byType, List<ByYear> byYear,
            List<? extends DocumentList.ByInitial> bySubjectInitial) implements DocumentList.Counts { }

    private record ByType(String type, int count) implements DocumentList.ByType { }

    private record ByYear(int year, int count) implements DocumentList.ByYear { }

    private record ByInitial(String initial, int count) implements DocumentList.ByInitial { }

    private record Document(String id, String title, String altTitle, String longType, int year, int number,
            List <? extends AltNumber> altNumbers, String cite, ZonedDateTime published, ZonedDateTime updated,
            String version, List<String> formats) implements DocumentList.Document { }

    private record AltNumber(String category, String value) implements uk.gov.legislation.util.AltNumber, DocumentList.Document.AltNumber { }

}
