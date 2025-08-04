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
            String number,
            String subject,
            String language,
            LocalDate published,
            String q,
            Parameters.Sort sort,
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
            .q(q)
            .sort(sort)
            .page(page)
            .pageSize(pageSize)
            .build();
        Parameters params2 = convert(params);
        String atom = db.getAtom(params2);
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
            String number,
            String subject,
            String language,
            LocalDate published,
            String q,
            Parameters.Sort sort,
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
            .q(q)
            .sort(sort)
            .page(page)
            .pageSize(pageSize)
            .build();
        Parameters params2 = convert(params);
        return Optional.of(db.get(params2))
            .map(results -> DocumentsFeedConverter.convert(results, params))
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private static Parameters convert(SearchParameters params) {
        var builder = Parameters.builder()
            .type(params.types)
            .year(params.year)
            .startYear(params.startYear)
            .endYear(params.endYear)
            .title(params.title)
            .subject(params.subject)
            .language(params.language)
            .published(params.published)
            .text(params.q)
            .sort(params.sort)
            .page(params.page)
            .pageSize(params.pageSize);
        try {
            NumberAndSeries.parse(params.number)
                .ifPresent(x -> builder.number(x.number(), x.series()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return builder.build();
    }

}
