package uk.gov.legislation.api.documents;

import uk.gov.legislation.data.marklogic.SearchResults;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeConverter {
    static TypeResponse convert(SearchResults results) {
        TypeResponse response = new TypeResponse();
        response.meta = meta(results);
        response.documents = documents(results.entries);
        return response;
    }

    private static TypeResponse.Meta meta(SearchResults results) {
        TypeResponse.Meta meta = new TypeResponse.Meta();
        meta.page = results.page;
        meta.pageSize = results.itemsPerPage;
        meta.totalPages = results.morePages;
        meta.updated = results.updated;
        meta.counts = counts(results);
        return meta;
    }

    private static TypeResponse.Counts counts(SearchResults results) {
        TypeResponse.Counts counts = new TypeResponse.Counts();
        if (results.facets.facetTypes.facetType != null)
            counts.total = results.facets.facetTypes.facetType.value;
        counts.yearly = yearly(results.facets.facetYears);
        return counts;
    }

    private static List<TypeResponse.Yearly> yearly(SearchResults.FacetYears facets) {
        return facets.entries.stream().map(TypeConverter::yearly).collect(Collectors.toList());
    }

    private static TypeResponse.Yearly yearly(SearchResults.FacetYear facet) {
        TypeResponse.Yearly yearly = new TypeResponse.Yearly();
        yearly.year = facet.year;
        yearly.count = facet.total;
        return yearly;
    }

    private static List<TypeResponse.Document> documents(List<SearchResults.Entry> entries) {
        if (entries == null)
            return Collections.emptyList();
        return entries.stream().map(TypeConverter::document).collect(Collectors.toList());
    }

    private static TypeResponse.Document document(SearchResults.Entry entry) {
        TypeResponse.Document doc = new TypeResponse.Document();
        doc.id = entry.id.substring(33);
        doc.title = entry.title;
        doc.longType = entry.mainType == null ? null : entry.mainType.value;
        doc.year = entry.year == null ? null : entry.year.value;
        doc.number = entry.number == null ? null : entry.number.value;
        doc.created = entry.creationDate == null ? null : entry.creationDate.date;
        return doc;
    }

}
