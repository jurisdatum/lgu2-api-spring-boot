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
import java.util.List;
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
            List<String> type,
            Integer year,
            Integer startYear,
            Integer endYear,
            Integer number,
            String subject,
            String language,
            LocalDate published,
            Integer page,
            Integer pageSize) throws IOException, InterruptedException {
        validateType(type);
        validateYears(year, startYear, endYear);
        validateTitle(title);
        validateLanguage(language);
        Parameters params = Parameters.builder()
            .type(type)
            .year(year)
            .startYear(startYear)
            .endYear(endYear)
            .number(number)
            .title(title)
            .subject(subject)
            .language(language)
            .published(published)
            .page(page)
            .build();
        String atom = db.getAtom(params);
        return ResponseEntity.ok(atom);
    }

    public static void validateYears(Integer year, Integer startYear, Integer endYear) {
        if (year != null && (startYear != null || endYear != null))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "`year` cannot be combined with `startYear` or `endYear`");
        if (startYear != null && endYear != null && startYear > endYear)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "`startYear` must be ≤ `endYear`");
    }

    @Override
    public ResponseEntity<PageOfDocuments> searchByJson(
            String title,
            List <String> type,
            Integer year,
            Integer startYear,
            Integer endYear,
            Integer number,
            String subject,
            String language,
            LocalDate published,
            Integer page,
            Integer pageSize) throws IOException, InterruptedException {
        validateType(type);
        validateYears(year, startYear, endYear);
        validateTitle(title);
        validateLanguage(language);
        SearchParameters params = SearchParameters.builder()
            .types(type)
            .year(year)
            .startYear(startYear)
            .endYear(endYear)
            .number(number)
            .title(title)
            .subject(subject)
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
