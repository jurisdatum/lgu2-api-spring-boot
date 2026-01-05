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
import static uk.gov.legislation.util.Stage.safeStage;

@RestController
public class SearchController implements SearchApi {

    private final Search db;

    public SearchController(Search db) {
        this.db = db;
    }

    @Override
    public ResponseEntity<String> searchByAtom(SearchParameters param)
        throws IOException, InterruptedException {

        validateSearchParameters(param);
        String atom = db.getAtom(convert(param));
        return ResponseEntity.ok(atom);
    }

    private static void validateSearchParameters(SearchParameters param) {
        validateType(param.getTypes());
        validateYears(param.getYear(), param.getStartYear(), param.getEndYear());
        validateTitle(param.getTitle());
        validateLanguage(param.getLanguage());
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

        validateSearchParameters(param);
        return Optional.of(db.get(convert(param)))
            .map(results -> DocumentsFeedConverter.convert(results, param))
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private static Parameters convert(SearchParameters params) {
        var builder = Parameters.builder()
            .type(params.getTypes())
            .year(params.getYear())
            .startYear(params.getStartYear())
            .endYear(params.getEndYear())
            .title(params.getTitle())
            .subject(params.getSubject())
            .language(params.getLanguage())
            .published(params.getPublished())
            .text(params.getQ())
            .sort(params.getSort())
            .extent(params.getExtent(), params.isExclusiveExtent())
            .stage(safeStage(params.getStage()))
            .department(params.getDepartment())
            .version(params.getPointInTime())
            .page(params.getPage())
            .pageSize(params.getPageSize());
        try {
            NumberAndSeries.parse(params.getNumber())
                .ifPresent(x -> builder.number(x.number(), x.series()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return builder.build();
    }

}
