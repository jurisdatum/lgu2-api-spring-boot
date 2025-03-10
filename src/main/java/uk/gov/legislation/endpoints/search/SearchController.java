package uk.gov.legislation.endpoints.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.io.IOException;

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
            int page,
            String language) throws IOException, InterruptedException {
        validateType(type);
        validateTitle(title);
        validateLanguage(language);
        String atom = db.getAtomByTitleAndTypeAndYearAndNumber(title, type, year, number, language, page);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<PageOfDocuments> searchByJson(
            String title,
            String type,
            Integer year,
            Integer number,
            int page,
            String language) throws IOException, InterruptedException {
        validateType(type);
        validateTitle(title);
        validateLanguage(language);
        SearchResults raw = db.getJsonByTitleAndTypeAndYearAndNumber(title, type, year, number, language, page);
        PageOfDocuments converted = DocumentsFeedConverter.convert(raw);
        return ResponseEntity.ok(converted);
    }

}
