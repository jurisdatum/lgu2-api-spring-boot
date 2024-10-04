package uk.gov.legislation.api.documents;

import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.Cites;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Converter {

    private static record Response(Meta meta, List<? extends DocumentList.Document> documents) implements DocumentList { }

    private static record Meta(int page, int pageSize, int totalPages, LocalDateTime updated, Counts counts) implements DocumentList.Meta { }

    private static record Counts(int total, List<ByType> byType, List<ByYear> yearly, DocumentList.Subjects subjects) implements DocumentList.Counts { }

    private static record ByType(String type, int count) implements DocumentList.ByType { }

    private static record ByYear(int year, int count) implements DocumentList.ByYear { }

    private static record Subjects (List<? extends DocumentList.ByInitial> byInitial, List<String> headings) implements DocumentList.Subjects { }

    private static record ByInitial(String initial, int count) implements DocumentList.ByInitial { }

    private static record Document(String id, String title, String altTitle, String longType, int year, int number, List<Converter.AltNumber> altNumbers, String cite, ZonedDateTime published, ZonedDateTime updated, String version) implements DocumentList.Document { }

    private static record AltNumber(String category, String value) implements uk.gov.legislation.util.AltNumber, DocumentList.Document.AltNumber { }

    static DocumentList convert(SearchResults results) {
        Meta meta = convertMeta(results);
        List<Document> docs = convertDocs(results);
        return new Response(meta, docs);
    }

    private static Meta convertMeta(SearchResults results) {
        Counts counts = convertCounts(results);
        return new Meta(results.page, results.itemsPerPage, results.morePages, results.updated, counts);
    }

    private static Counts convertCounts(SearchResults results) {
        int total = results.facets.facetYears.entries.stream().mapToInt(f -> f.total).sum();
        List<ByType> byType = convertTypeFacets(results.facets.facetTypes);
        List<ByYear> byYear = convertYearFacets(results.facets.facetYears);
        DocumentList.Subjects bySubject = convertSubjectFacets(results.facets.subjects);
        return new Counts(total, byType, byYear, bySubject);
    }

    private static List<ByType> convertTypeFacets(SearchResults.FacetTypes facetTypes) {
        if (facetTypes == null)
            return Collections.emptyList();
        if (facetTypes.entries == null)
            return Collections.emptyList();
        return facetTypes.entries.stream().map(f -> new ByType(f.type, f.value)).collect(Collectors.toList());
    }

    private static List<ByYear> convertYearFacets(SearchResults.FacetYears years) {
        if (years == null)
            return Collections.emptyList();
        if (years.entries == null)
            return Collections.emptyList();
        return years.entries.stream().map(f -> new ByYear(f.year, f.total)).collect(Collectors.toList());
    }

    private static DocumentList.Subjects convertSubjectFacets(SearchResults.Subjects subjects) {
        if (subjects == null)
            return null;
        List<? extends DocumentList.ByInitial> byInitial;
        if (subjects.initials == null)
            byInitial = null;
        else
            byInitial = subjects.initials.stream().map(i -> new ByInitial(i.initial, i.total)).toList();
        List<String> headings;
        if (subjects.headings == null)
            headings = null;
        else
            headings = subjects.headings.stream().map(h -> h.name).toList();
        return new Subjects(byInitial, headings);
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

    private static Pattern Version = Pattern.compile("/([^/]+)/revision$");

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
