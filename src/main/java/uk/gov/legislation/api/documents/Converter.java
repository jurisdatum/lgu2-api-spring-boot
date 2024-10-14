package uk.gov.legislation.api.documents;

import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.Cites;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Converter {

    private record Response(Meta meta, List<? extends DocumentList.Document> documents) implements DocumentList { }

    private record Meta(int page, int pageSize, int totalPages, LocalDateTime updated, Counts counts, SortedSet<String> subjects) implements DocumentList.Meta { }

    private record Counts(int total, List<ByType> byType, List<ByYear> byYear, List<? extends DocumentList.ByInitial> bySubjectInitial) implements DocumentList.Counts { }

    private record ByType(String type, int count) implements DocumentList.ByType { }

    private record ByYear(int year, int count) implements DocumentList.ByYear { }

    private record ByInitial(String initial, int count) implements DocumentList.ByInitial { }

    private record Document(String id, String title, String altTitle, String longType, int year, int number, List<Converter.AltNumber> altNumbers, String cite, ZonedDateTime published, ZonedDateTime updated, String version) implements DocumentList.Document { }

    private record AltNumber(String category, String value) implements uk.gov.legislation.util.AltNumber, DocumentList.Document.AltNumber { }

    static DocumentList convert(SearchResults results) {
        Meta meta = convertMeta(results);
        List<Document> docs = convertDocs(results);
        return new Response(meta, docs);
    }

    private static Meta convertMeta(SearchResults results) {
        Counts counts = convertCounts(results);
        SortedSet<String> subjects = getSubjectHeadings(results.facets.subjects);
        return new Meta(results.page, results.itemsPerPage, results.morePages, results.updated, counts, subjects);
    }

    private static Counts convertCounts(SearchResults results) {
        int total = results.facets.facetYears.entries.stream().mapToInt(f -> f.total).sum();
        List<ByType> byType = convertTypeFacets(results.facets.facetTypes);
        List<ByYear> byYear = convertYearFacets(results.facets.facetYears);
        List<ByInitial> bySubject = convertSubjectFacets(results.facets.subjects);
        return new Counts(total, byType, byYear, bySubject);
    }

    private static List<ByType> convertTypeFacets(SearchResults.FacetTypes facetTypes) {
        if (facetTypes == null)
            return Collections.emptyList();
        if (facetTypes.entries == null)
            return Collections.emptyList();
        return facetTypes.entries.stream()
                .filter(f -> !f.type.equals("UnitedKingdomDraftPublicBill")) // ToDo !?
                .map(f -> new ByType(f.type, f.value)).collect(Collectors.toList());
    }

    private static List<ByYear> convertYearFacets(SearchResults.FacetYears years) {
        if (years == null)
            return Collections.emptyList();
        if (years.entries == null)
            return Collections.emptyList();
        return years.entries.stream().map(f -> new ByYear(f.year, f.total)).collect(Collectors.toList());
    }

    private static List<ByInitial> convertSubjectFacets(SearchResults.Subjects subjects) {
        if (subjects == null)
            return null;
        if (subjects.initials == null)
            return null;
        return subjects.initials.stream().map(i -> new ByInitial(i.initial, i.total)).toList();
    }

    private static SortedSet<String> getSubjectHeadings(SearchResults.Subjects subjects) {
        if (subjects == null)
            return null;
        if (subjects.headings == null)
            return null;
        return subjects.headings.stream().map(h -> h.name).collect(Collectors.toCollection(TreeSet::new));
    }

    private static List<Document> convertDocs(SearchResults results) {
        if (results.entries == null)
            return Collections.emptyList();
        return results.entries.stream().map(Converter::convertDoc).collect(Collectors.toList());
    }

    private static Document convertDoc(SearchResults.Entry entry) {
        String id = entry.id.substring(33);
        String longType = entry.mainType == null ? null : entry.mainType.value;
        int year = entry.year == null ? 0 : entry.year.value;
        int number = entry.number == null ? 0 : entry.number.value;
        List<AltNumber> altNumbers = entry.altNumbers == null ? null : entry.altNumbers.stream().map(a -> new AltNumber(a.Category, a.Value)).collect(Collectors.toList());
        String cite = Cites.make(longType, year, number, altNumbers);
        String title = entry.title;
        String altTitle = entry.altTitle;
        ZonedDateTime published = entry.published;
        ZonedDateTime updated = entry.updated;
        String version = getVersion(entry.links);
        return new Document(id, title, altTitle, longType, year, number, altNumbers, cite, published, updated, version);
    }

    private static final Pattern Version = Pattern.compile("/([^/]+)/revision$");

    private static String getVersion(List<SearchResults.Link> links) {
        Optional<SearchResults.Link> link = links.stream().filter(l -> l.rel == null).findFirst();
        if (link.isEmpty())
            return null;
        Matcher matcher = Version.matcher(link.get().href);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

}
