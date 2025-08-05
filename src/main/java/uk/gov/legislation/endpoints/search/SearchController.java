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
import java.util.Optional;

import static uk.gov.legislation.endpoints.ParameterValidator.*;

@RestController
public class SearchController implements SearchApi {

    private final Search db;

    public SearchController(Search db) {
        this.db = db;
    }

    @Override
    public ResponseEntity<String> searchByAtom(SearchParameters param)
        throws IOException, InterruptedException {

        validateType(param.types);
        validateYears(param.year, param.startYear, param.endYear);
        validateTitle(param.title);
        validateLanguage(param.language);
        String atom = db.getAtom(convert(param));
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
    public ResponseEntity<PageOfDocuments> searchByJson(SearchParameters param)
        throws IOException, InterruptedException {

        validateType(param.types);
        validateYears(param.year, param.startYear, param.endYear);
        validateTitle(param.title);
        validateLanguage(param.language);

        return Optional.of(db.get(convert(param)))
            .map(results -> DocumentsFeedConverter.convert(results, param))
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
