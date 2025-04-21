package uk.gov.legislation.data2;

import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.io.IOException;

@Service
public class SearchService {

    private final Search marklogic;

    public SearchService(Search marklogic) {
        this.marklogic = marklogic;
    }

    /* by type */

    public String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        Parameters params = Parameters.builder().type(type).page(page).build();
        return marklogic.getAtom(params);
    }

    public PageOfDocuments byType(String type, int page) throws IOException, InterruptedException {
        Parameters params = Parameters.builder().type(type).page(page).build();
        SearchResults results = marklogic.get(params);
        return DocumentsFeedConverter.convert(results);
    }

    /* by type and year */

    public String byTypeAndYearAtom(String type, int year, int page) throws IOException, InterruptedException {
        Parameters params = Parameters.builder().type(type).year(year).page(page).build();
        return marklogic.getAtom(params);
    }

    public PageOfDocuments byTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        Parameters params = Parameters.builder().type(type).year(year).page(page).build();
        SearchResults results = marklogic.get(params);
        return DocumentsFeedConverter.convert(results);
    }

    /* new legislation */

    private static Parameters paramsForNewLegislation(String region, int page) {
        return Parameters.builder()
            .type(region)
            .sort(Parameters.Sort.PUBLISHED)
            .page(page)
            .build();
    }

    public String getNewAtom(String region, int page) throws IOException, InterruptedException {
        Parameters params = paramsForNewLegislation(region, page);
        return marklogic.getAtom(params);
    }

    public PageOfDocuments getNew(String region, int page) throws IOException, InterruptedException {
        Parameters params = paramsForNewLegislation(region, page);
        SearchResults results = marklogic.get(params);
        return DocumentsFeedConverter.convert(results);
    }

}
