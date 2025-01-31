package uk.gov.legislation.data.marklogic.search;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.util.Type;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class Search {

    private static final String ENDPOINT = "search.xq";

    private final MarkLogic db;

    public Search(MarkLogic db) {
        this.db = db;
    }

    /* fetch by title, type, year and number */

    public String getAtomByTitleAndTypeAndYearAndNumber(String title, String type, Integer year, Integer number, String language, int page) throws IOException, InterruptedException {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("?page=").append(page);

        if (title != null && !title.isBlank()) {
            queryBuilder.append("&title=").append(URLEncoder.encode(title, StandardCharsets.UTF_8));
        }
        if (type != null && !type.isBlank()) {
            queryBuilder.append("&type=").append(URLEncoder.encode(type, StandardCharsets.US_ASCII));
            if (Type.WSI.shortName().equals(type)) {
                queryBuilder.append("&series=w");
            } else if (Type.NISI.shortName().equals(type)) {
                queryBuilder.append("&series=ni");
            }
        }
        if (year != null) {
            queryBuilder.append("&year=").append(year);
        }
        if (number != null) {
            queryBuilder.append("&number=").append(number);
        }
        if (language != null) {
            queryBuilder.append("&lang=").append(language);
        }
        String query = queryBuilder.toString();
        return db.get(ENDPOINT, query);
    }

    public SearchResults getJsonByTitleAndTypeAndYearAndNumber(String title, String type, Integer year, Integer number, String language, int page) throws IOException, InterruptedException {
        String atom = getAtomByTitleAndTypeAndYearAndNumber(title, type, year, number, language, page);
        return SearchResults.parse(atom);
    }

    /* fetch by type and year */

    public String byTypeAndYearAtom(String type, int year, int page) throws IOException, InterruptedException {
        return getAtomByTitleAndTypeAndYearAndNumber(null, type, year, null, null, page);
    }
    public SearchResults byTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        return getJsonByTitleAndTypeAndYearAndNumber(null, type, year, null, null, page);
    }

    /* fetch by type */

    public String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        return getAtomByTitleAndTypeAndYearAndNumber(null, type, null, null, null, page);
    }
    public SearchResults byType(String type, int page) throws IOException, InterruptedException {
        return getJsonByTitleAndTypeAndYearAndNumber(null, type, null, null, null, page);
    }

}
