package uk.gov.legislation.endpoints.dates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.DateCount;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "Dates")
@RequestMapping(
    path = "/dates",
    produces = "application/json"
)
public class DatesController {

    private final Search search;

    public DatesController(Search search) {
        this.search = search;
    }

    @GetMapping("/published")
    @Operation(summary = "Dates of recent publication, with the number of documents published on each")
    public List<DateCount> published() throws IOException, InterruptedException {
        LocalDate date = LocalDate.now().minusDays(1);
        Parameters params = Parameters.builder().published(date).build();
        return search.get(params).facets.facetPublishDates.stream()
            .map(DateCount::new).toList();
    }

}
