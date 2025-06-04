package uk.gov.legislation.endpoints.dates;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.custom.RecentPublishedDates;

import java.util.List;

@RestController
@Tag(name = "Dates")
@RequestMapping(
    path = "/dates",
    produces = "application/json"
)
public class DatesController {

    private final RecentPublishedDates query;

    public DatesController(RecentPublishedDates query) {
        this.query = query;
    }

    @GetMapping("/published")
    public List<String> published() {
        return query.fetch();
    }

}
