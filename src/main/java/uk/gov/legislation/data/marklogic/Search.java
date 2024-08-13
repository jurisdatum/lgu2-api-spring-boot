package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public static String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type) + "&page=" + page;
        URI uri;
        try {
            uri = new URI(Endpoint + query);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
        String query = "?type=" + URLEncoder.encode(type) + "&year=" + year + "&page=" + page;;
        URI uri;
        try {
            uri = new URI(Endpoint + query);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return MarkLogic.get(uri);
    }

}
