package uk.gov.legislation.endpoints.dates;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.DateCount;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

@RestController
public class DatesController implements DatesApi {

    // UK legislation dates are relative to the UK day, not the server's zone.
    private static final ZoneId LONDON = ZoneId.of("Europe/London");

    private final Search search;

    public DatesController(Search search) {
        this.search = search;
    }

    @Override
    public List<DateCount> published() throws IOException, InterruptedException {
        LocalDate date = LocalDate.now(LONDON).minusDays(1);
        Parameters params = Parameters.builder().published(date).build();
        SearchResults.Facets facets = search.get(params).facets;
        // MarkLogic omits the <facets>/<facetPublishDates> elements entirely when nothing was
        // published on the queried day, so Jackson leaves these null (not empty). Guard both
        // levels and return an empty list rather than throwing.
        if (facets == null || facets.facetPublishDates == null) {
            return List.of();
        }
        return facets.facetPublishDates.stream().map(DateCount::new).toList();
    }
}
