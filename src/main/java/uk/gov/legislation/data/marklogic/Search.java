package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.legislation.util.Type;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class Search {

    static final String Endpoint = "search.xq";

    @Autowired
    private MarkLogic db;

    public SearchResults byType(String type, int page) throws IOException, InterruptedException {
        String xml = byTypeAtom(type, page);
        try {
            return SearchResults.parse(xml);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // series can be 'w', 's', 'ni', 'l', 'c'

    public String byTypeAtom(String type, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) + "&page=" + page;
        if (type.equals(Type.WSI.shortName()))
            query += "&series=w";
        else if (type.equals(Type.NISI.shortName()))
            query += "&series=ni";
        return db.get(Endpoint, query);
    }

    public SearchResults byTypeAndYear(String type, int year, int page) throws IOException, InterruptedException {
        String xml = byTypeAndYearAtom(type, year, page);
        try {
            return SearchResults.parse(xml);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String byTypeAndYearAtom(String type, int year, int page) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) + "&year=" + year + "&page=" + page;
        if (type.equals(Type.WSI.shortName()))
            query += "&series=w";
        else if (type.equals(Type.NISI.shortName()))
            query += "&series=ni";
        return db.get(Endpoint, query);
    }

}
