package uk.gov.legislation.api.documents;

import uk.gov.legislation.data.marklogic.SearchResults;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class SearchResultsConverter {
    static Response convert(SearchResults results) {
        Response response = new Response();
        response.meta = meta(results);
        response.documents = documents(results.entries);
        return response;
    }

    private static Response.Meta meta(SearchResults results) {
        Response.Meta meta = new Response.Meta();
        meta.page = results.page;
        meta.pageSize = results.itemsPerPage;
        meta.totalPages = results.morePages;
        meta.updated = results.updated;
        meta.counts = counts(results);
        return meta;
    }

    private static Response.Counts counts(SearchResults results) {
        Response.Counts counts = new Response.Counts();
        if (results.facets.facetTypes.facetType != null)
            counts.total = results.facets.facetTypes.facetType.value;
        counts.yearly = yearly(results.facets.facetYears);
        return counts;
    }

    private static List<Response.Yearly> yearly(SearchResults.FacetYears facets) {
        return facets.entries.stream().map(SearchResultsConverter::yearly).collect(Collectors.toList());
    }

    private static Response.Yearly yearly(SearchResults.FacetYear facet) {
        Response.Yearly yearly = new Response.Yearly();
        yearly.year = facet.year;
        yearly.count = facet.total;
        return yearly;
    }

    private static List<Response.Document> documents(List<SearchResults.Entry> entries) {
        if (entries == null)
            return Collections.emptyList();
        return entries.stream().map(SearchResultsConverter::document).collect(Collectors.toList());
    }

    private static Response.Document document(SearchResults.Entry entry) {
        Response.Document doc = new Response.Document();
        doc.id = entry.id.substring(33);
        doc.title = entry.title;
        doc.longType = entry.mainType == null ? null : entry.mainType.value;
        doc.year = entry.year == null ? null : entry.year.value;
        doc.number = entry.number == null ? null : entry.number.value;
        doc.created = entry.creationDate == null ? null : entry.creationDate.date;
        doc.version = getVersion(entry.links);
        return doc;
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
