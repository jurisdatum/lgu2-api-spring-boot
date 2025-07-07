package uk.gov.legislation.endpoints.search;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.util.Alphabet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Search")
public interface SearchApi {

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    ResponseEntity<String> searchByAtom(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) LocalDate published,
            @RequestParam(required = false) @Parameter(description = "Sort by")Parameters.Sort sort,
            @RequestParam(required = false)
            @Parameter(description = "Select First Letter of Heading") Alphabet initialLetter,
            @RequestParam(required = false) String heading,
            @RequestParam(required = false) @Parameter(schema = @Schema(defaultValue = "1")) Integer page,
            @RequestParam(required = false) @Parameter(schema = @Schema(defaultValue = "20")) Integer pageSize
    ) throws IOException, InterruptedException;


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PageOfDocuments> searchByJson(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer number,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) LocalDate published,
            @RequestParam(required = false) @Parameter(description = "Sort by")Parameters.Sort sort,
            @RequestParam(required = false)
            @Parameter(description = "Select First Letter of Heading") Alphabet initialLetter,
            @RequestParam(required = false) String heading,
            @RequestParam(required = false) @Parameter(schema = @Schema(defaultValue = "1")) Integer page,
            @RequestParam(required = false) @Parameter(schema = @Schema(defaultValue = "20")) Integer pageSize
    ) throws IOException, InterruptedException;

}
