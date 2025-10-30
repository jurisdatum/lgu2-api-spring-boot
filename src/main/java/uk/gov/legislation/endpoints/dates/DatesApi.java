package uk.gov.legislation.endpoints.dates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.legislation.api.responses.DateCount;

import java.io.IOException;
import java.util.List;
@Tag(name = "Dates")
@RequestMapping("/dates")
public interface DatesApi {

    @GetMapping(path = "/published", produces = "application/json")
    @Operation(summary = "Dates of recent publication, with the number of documents published on each")
     List <DateCount> published() throws IOException, InterruptedException;
}
