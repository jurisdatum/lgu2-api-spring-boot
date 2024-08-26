package uk.gov.legislation.data.marklogic;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDocument {

    static final String Endpoint = MarkLogic.BASE + "legislation.xq";

    public static String getDocument(String type, int year, int number) throws IOException, InterruptedException, NoDocumentException {
        return getDocument(type, Integer.toString(year), Integer.toString(number));
    }
    private static String getDocument(String type, String year, String number) throws IOException, InterruptedException, NoDocumentException {
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
            throw new NoDocumentException(error);
        if (!error.header.name.equals("Location"))
            throw new NoDocumentException(error);
        Pattern pattern = Pattern.compile("/([^/]+)/revision$");
        Matcher matcher = pattern.matcher(error.header.value);
        if (!matcher.find())
            throw new RuntimeException();
        String version = matcher.group(1);
        return getVersion(type, year, number, version);
    }

    public static String getVersion(String type, int year, int number, String version) throws IOException, InterruptedException, NoDocumentException {
        return getVersion(type, Integer.toString(year), Integer.toString(number), version);
    }
    private static String getVersion(String type, String year, String number, String version) throws IOException, InterruptedException, NoDocumentException {
        String query = "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + URLEncoder.encode(year, StandardCharsets.US_ASCII) +
            "&number=" + URLEncoder.encode(number, StandardCharsets.US_ASCII) +
            "&version=" + URLEncoder.encode(version, StandardCharsets.US_ASCII);
        URI uri = URI.create(Endpoint + query);
        String xml = MarkLogic.get(uri);
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        throw new NoDocumentException(error);
    }

}
