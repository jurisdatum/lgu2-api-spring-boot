package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.legislation.util.Type;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

public class Search {

    static final String Endpoint = MarkLogic.BASE + "search.xq";

    public static SearchResults byType(String type, int page) throws IOException, InterruptedException {
        String xml = byTypeAtom(type, page);
        try {
            return SearchResults.parse(xml);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // series can be 'w', 's', 'ni', 'l', 'c'

    public static String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type) + "&page=" + page;
        if (type.equals(Type.WSI.shortName()))
            query += "&series=w";
        else if (type.equals(Type.NISI.shortName()))
            query += "&series=ni";
        URI uri = URI.create(Endpoint + query);
        return MarkLogic.get(uri);
    }

    public static SearchResults byTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        String xml = byTypeAndYearAtom(type, year, page);
        try {
            return SearchResults.parse(xml);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String byTypeAndYearAtom(String type, int year, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type) + "&year=" + year + "&page=" + page;
        if (type.equals(Type.WSI.shortName()))
            query += "&series=w";
        else if (type.equals(Type.NISI.shortName()))
            query += "&series=ni";
        URI uri = URI.create(Endpoint + query);
        return MarkLogic.get(uri);
    }

}
