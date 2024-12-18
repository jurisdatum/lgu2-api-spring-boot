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
