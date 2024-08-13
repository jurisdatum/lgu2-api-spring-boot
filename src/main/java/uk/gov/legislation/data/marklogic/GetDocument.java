package uk.gov.legislation.data.marklogic;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDocument {

    static final String Endpoint = MarkLogic.BASE + "legislation.xq";

    public static String getDocument(String type, int year, int number) throws IOException, InterruptedException {
        return getDocument(type, Integer.toString(year), Integer.toString(number));
    }

    private static String getDocument(String type, String year, String number) throws IOException, InterruptedException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + URLEncoder.encode(year, StandardCharsets.US_ASCII) +
            "&number=" + URLEncoder.encode(number, StandardCharsets.US_ASCII);
        URI uri = URI.create(Endpoint + query);
        String xml = MarkLogic.get(uri);
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        if (error.statusCode != 307)
            throw new ResponseStatusException(HttpStatus.valueOf(error.statusCode), error.message);
        if (!error.header.name.equals("Location"))
            throw new ResponseStatusException(HttpStatus.valueOf(error.statusCode), error.message);
        Pattern pattern = Pattern.compile("/([^/]+)/revision$");
        Matcher matcher = pattern.matcher(error.header.value);
        if (!matcher.find())
            throw new RuntimeException();
        String version = matcher.group(1);
        uri = URI.create(uri.toString() + "&version=" + URLEncoder.encode(version, StandardCharsets.US_ASCII));
        xml = MarkLogic.get(uri);
        return xml;
    }

}
