package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.CommonMetadata;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.Links;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DocumentsFeedConverter {

    public static PageOfDocuments convert(SearchResults atom) {
        PageOfDocuments page = new PageOfDocuments();
        page.meta = convertMeta(atom);
        page.documents = convertDocuments(atom.entries);
        return page;
    }

    private static PageOfDocuments.Meta convertMeta(SearchResults atom) {
        PageOfDocuments.Meta meta = new PageOfDocuments.Meta();
        meta.page = atom.page;
        meta.pageSize = atom.itemsPerPage;
        meta.totalPages = atom.morePages;
        meta.updated = atom.updated;
        meta.counts = convertCounts(atom.facets);
        meta.subjects = convertSubjects(atom.facets.subjects);
        return meta;
    }

    private static PageOfDocuments.Counts convertCounts(SearchResults.Facets facets) {
        PageOfDocuments.Counts counts = new PageOfDocuments.Counts();
        counts.total = facets.facetYears.entries.stream()
                .mapToInt(facet -> facet.total)
                .sum();
        counts.byType = Facets.convertTypeFacets(facets.facetTypes);
        counts.byYear = Facets.convertYearFacets(facets.facetYears);
        counts.bySubjectInitial = Facets.convertSubjectFacets(facets.subjects);
        return counts;
    }

    private static SortedSet<String> convertSubjects(SearchResults.Subjects subjects) {
        if (subjects == null)
            return Collections.emptySortedSet();
        if (subjects.headings == null)
            return Collections.emptySortedSet();
        return subjects.headings.stream()
            .map(heading -> heading.name)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private static List<PageOfDocuments.Document> convertDocuments(List<SearchResults.Entry> entries) {
        if (entries == null)
            return Collections.emptyList();
        return entries.stream()
            .map(DocumentsFeedConverter::convertDocument)
            .toList();
    }

    private static PageOfDocuments.Document convertDocument(SearchResults.Entry entry) {
        PageOfDocuments.Document doc = new PageOfDocuments.Document();
        doc.id = entry.id.substring(33);
        doc.longType = entry.mainType == null ? null : entry.mainType.value;
        doc.year = entry.year == null ? 0 : entry.year.value;
        doc.number = entry.number == null ? 0 : entry.number.value;
        doc.altNumbers = convertAltNumbers(entry.altNumbers);
        doc.cite = Cites.convertNumbersAndMake(doc.longType, doc.year, doc.number, doc.altNumbers);
        doc.title = entry.title;
        doc.altTitle = entry.altTitle;
        doc.published = entry.published;
        doc.updated = entry.updated;
        doc.version = getVersion(entry.links);
        doc.formats = getFormats(entry.links);
        return doc;
    }

    /* alt numbers */

    private static List<CommonMetadata.AltNumber> convertAltNumbers(List<SearchResults.Entry.AlternativeNumber> altNumbers) {
        if (altNumbers == null)
            return Collections.emptyList();
        return altNumbers.stream().map(DocumentsFeedConverter::convertAltNumber).toList();
    }

    private static CommonMetadata.AltNumber convertAltNumber(SearchResults.Entry.AlternativeNumber atom) {
        CommonMetadata.AltNumber altNumber = new CommonMetadata.AltNumber();
        altNumber.category = atom.Category;
        altNumber.value = atom.Value;
        return altNumber;
    }

    /* links */

    private static String getVersion(List<SearchResults.Link> links) {
        return links.stream()
            .filter(link -> link.rel == null)
            .findFirst()
            .map(link -> link.href)
            .map(Links::parse)
            .flatMap(Links.Components::version)
            .orElse(null);
    }

    private static List<String> getFormats(List<SearchResults.Link> links) {
        return links.stream()
            .filter(l -> "alternate".equals(l.rel))
            .map(l -> l.type)
            .filter(t -> "application/xml".equals(t) || "application/pdf".equals(t))
            .map(t -> t.substring(12))
            .toList();
    }

}
