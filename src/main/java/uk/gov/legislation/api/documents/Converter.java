package uk.gov.legislation.api.documents;

import uk.gov.legislation.data.marklogic.SearchResults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Converter {

    private static record Response(Meta meta, List<? extends DocumentList.Document> documents) implements DocumentList { }

    private static record Meta(int page, int pageSize, int totalPages, LocalDateTime updated, Counts counts) implements DocumentList.Meta { }

    private static record Counts(int total, List<Yearly> yearly) implements DocumentList.Counts { }

    private static record Yearly(int year, int count) implements DocumentList.Yearly { }

    private static record Document(String id, String title, String longType, String year, String number, LocalDate created, String version) implements DocumentList.Document { }

    static DocumentList convert(SearchResults results) {
        Meta meta = convertMeta(results);
        List<Document> docs = convertDocs(results);
        return new Response(meta, docs);
    }

    private static Meta convertMeta(SearchResults results) {
        LocalDateTime updated = null;
        Counts counts = convertCounts(results);
        return new Meta(results.page, results.itemsPerPage, results.morePages, updated, counts);
    }

    private static Counts convertCounts(SearchResults results) {
        int total = results.facets.facetTypes.facetType == null ? 0 : results.facets.facetTypes.facetType.value;
        List<Yearly> yearly = results.facets.facetYears.entries.stream()
            .map(facet -> new Yearly(facet.year, facet.total)).collect(Collectors.toList());
        return new Counts(total, yearly);
    }

    private static List<Document> convertDocs(SearchResults results) {
        if (results.entries == null)
            return Collections.emptyList();
        return results.entries.stream().map(Converter::convertDoc).collect(Collectors.toList());
    }

    private static Document convertDoc(SearchResults.Entry entry) {
        String id = entry.id.substring(33);
        String longType = entry.mainType == null ? null : entry.mainType.value;
        String year = entry.year == null ? null : entry.year.value;
        String number = entry.number == null ? null : entry.number.value;
        String title = entry.title;
        LocalDate created;
        try {
            created = LocalDate.parse(entry.creationDate.date);
        } catch (Exception e) {
            created = null;
        }
        String version = getVersion(entry.links);
        return new Document(id, title, longType, year, number, created, version);
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
