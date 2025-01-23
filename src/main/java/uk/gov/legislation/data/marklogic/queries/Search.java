package uk.gov.legislation.data.marklogic.queries;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.data.marklogic.SearchResults;
import uk.gov.legislation.util.Type;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class Search {

    private static final String ENDPOINT = "search.xq";

    private final MarkLogic db;

    /**
     * Constructor to initialize the Search service with a database instance.
     *
     * @param db the MarkLogic database instance
     */
    public Search(MarkLogic db) {
        this.db = db;
    }

    /**
     * Fetches search results by type and page.
     */
    public SearchResults byType(String type, int page) throws IOException, InterruptedException {
        String xml = byTypeAtom(type, page);
        return SearchResults.parse(xml);
    }

    /**
     * Fetches search results by title and page.
     */

    public SearchResults byTitleJson(String title, int page) throws IOException, InterruptedException {
        String xml = byTitleAtom(title, page);
        return SearchResults.parse(xml);
    }

    public SearchResults getJsonByTitleAndTypeAndYearAndNumber(String title, String type, String year, String number, int page) throws IOException, InterruptedException {
        String xml = searchFilterQuery(title,type,year,number, page);
        return SearchResults.parse(xml);
    }
    /**
     *  fetching search results by title, type, year and number.
     */
    public String getAtomByTitleAndTypeAndYearAndNumber(String title, String type, String year, String number, int page) throws IOException, InterruptedException {
        return searchFilterQuery(title,type,year,number, page);
    }

    // series can be 'w', 's', 'ni', 'l', 'c'

    /**
     * Constructs the query for fetching search results by type and page.
     */
    public String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) + "&page=" + page;
        if (Type.WSI.shortName().equals(type)) {
            query += "&series=w";
        } else if (Type.NISI.shortName().equals(type)) {
            query += "&series=ni";
        }
        return db.get(ENDPOINT, query);
    }

    /**
     * Constructs the query for fetching search results by title and page.
     */
    public String byTitleAtom(String title, int page) throws IOException, InterruptedException {
        String query = "?title=" + URLEncoder.encode(title, StandardCharsets.UTF_8) + "&page=" + page;
        return db.get(ENDPOINT, query);
    }
    /**
     * Constructs the query for fetching search results by title, type, year and number.
     */
    public String searchFilterQuery(String title, String type,String year,String number, int page) throws IOException, InterruptedException {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("?page=").append(page);

        if (title != null && !title.isEmpty()) {
            queryBuilder.append("&title=").append(URLEncoder.encode(title, StandardCharsets.UTF_8));
        }
        if (type != null && !type.isEmpty()) {
            queryBuilder.append("&type=").append(URLEncoder.encode(type, StandardCharsets.US_ASCII));
        }
        if (year != null && !year.isEmpty()) {
            queryBuilder.append("&year=").append(URLEncoder.encode(year, StandardCharsets.UTF_8));
        }
        if (number != null && !number.isEmpty()) {
            queryBuilder.append("&number=").append(URLEncoder.encode(number, StandardCharsets.UTF_8));
        }
        String query = queryBuilder.toString();
        return db.get(ENDPOINT, query);

    }

    /**
     * Fetches search results by type, year, and page.
     */
    public SearchResults byTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        String xml = byTypeAndYearAtom(type, year, page);
        return SearchResults.parse(xml);
    }

    /**
     * Constructs the query for fetching search results by type, year, and page.
     */
    public String byTypeAndYearAtom(String type, int year, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) + "&year=" + year + "&page=" + page;
        if (Type.WSI.shortName().equals(type)) {
            query += "&series=w";
        } else if (Type.NISI.shortName().equals(type)) {
            query += "&series=ni";
        }
        return db.get(ENDPOINT, query);
    }

}
