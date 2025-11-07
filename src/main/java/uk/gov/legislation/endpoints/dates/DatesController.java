package uk.gov.legislation.endpoints.dates;


import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.DateCount;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class DatesController implements DatesApi{

    private final Search search;

    public DatesController(Search search) {
        this.search = search;
    }

     @Override
     public List<DateCount> published() throws IOException, InterruptedException {
        LocalDate date = LocalDate.now().minusDays(1);
        Parameters params = Parameters.builder().published(date).build();
        return search.get(params).facets.facetPublishDates.stream()
            .map(DateCount::new).toList();
    }

}
