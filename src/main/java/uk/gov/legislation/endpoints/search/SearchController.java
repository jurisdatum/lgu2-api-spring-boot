package uk.gov.legislation.endpoints.search;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.*;

@RestController
public class SearchController implements SearchApi {

    private final Search db;

    public SearchController(Search db) {
        this.db = db;
    }

    @Override
    public ResponseEntity<String> searchByAtom(
            String title,
            String type,
            Integer year,
            Integer number,
            String language,
            LocalDate published,
            Integer page,
            Integer pageSize) throws IOException, InterruptedException {
        validateType(type);
        validateTitle(title);
        validateLanguage(language);
        Parameters params = Parameters.builder()
            .type(type)
            .year(year)
            .number(number)
            .title(title)
            .language(language)
            .published(published)
            .page(page)
            .build();
        String atom = db.getAtom(params);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<PageOfDocuments> searchByJson(
            String title,
            String type,
            Integer year,
            Integer number,
            String language,
            LocalDate published,
            Integer page,
            Integer pageSize) throws IOException, InterruptedException {
        validateType(type);
        validateTitle(title);
        validateLanguage(language);
        SearchParameters params = SearchParameters.builder()
            .type(type)
            .year(year)
            .number(number)
            .title(title)
            .language(language)
            .published(published)
            .page(page)
            .pageSize(pageSize)
            .build();
        return Optional.of(db.get(params.convert()))
            .map(results -> DocumentsFeedConverter.convert(results, params))
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
