package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.util.Collections;
import java.util.List;

public class Facets {

    static List<PageOfDocuments.ByType> convertTypeFacets(SearchResults.FacetTypes facetTypes) {
        if (facetTypes == null)
            return Collections.emptyList();
        if (facetTypes.entries == null)
            return Collections.emptyList();
        return facetTypes.entries.stream()
            .filter(f -> !f.type.equals("UnitedKingdomDraftPublicBill")) // TokDo !?
            .map(Facets::make1).toList();
    }

    private static PageOfDocuments.ByType make1(SearchResults.FacetType atom) {
        PageOfDocuments.ByType byType = new PageOfDocuments.ByType();
        byType.type = atom.type;
        byType.count = atom.value;
        return byType;
    }

    static List<PageOfDocuments.ByYear> convertYearFacets(SearchResults.FacetYears years) {
        if (years == null)
            return Collections.emptyList();
        if (years.entries == null)
            return Collections.emptyList();
        return years.entries.stream().map(Facets::make2).toList();
    }

    private static PageOfDocuments.ByYear make2(SearchResults.FacetYear atom) {
        PageOfDocuments.ByYear byYear = new PageOfDocuments.ByYear();
        byYear.year = atom.year;
        byYear.count = atom.total;
        return byYear;
    }

    static List<PageOfDocuments.ByInitial> convertSubjectFacets(SearchResults.Subjects subjects) {
        if (subjects == null)
            return Collections.emptyList();
        if (subjects.initials == null)
            return Collections.emptyList();
        return subjects.initials.stream().map(Facets::make3).toList();
    }

    private static PageOfDocuments.ByInitial make3(SearchResults.SubjectInitial atom) {
        PageOfDocuments.ByInitial byInitial = new PageOfDocuments.ByInitial();
        byInitial.initial = atom.initial;
        byInitial.count = atom.total;
        return byInitial;
    }

}
