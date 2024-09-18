package uk.gov.legislation.data.marklogic;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Legislation {

    static final String Endpoint = MarkLogic.BASE + "legislation.xq";

    public static String getDocument(String type, int year, int number, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number;
        return getOrRedirect(query, version);
    }

    public static String getTableOfContents(String type, int year, int number, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number +
            "&view=contents";
        return getOrRedirect(query, version);
    }

    public static String getDocumentSection(String type, int year, int number, String section, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number +
            "&section=" + URLEncoder.encode(section, StandardCharsets.US_ASCII);
        return getOrRedirect(query, version);
    }

    private static String getOrRedirect(String query, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        if (version.isPresent())
            query += "&version=" + URLEncoder.encode(version.get(), StandardCharsets.US_ASCII);
        URI uri = URI.create(Endpoint + query);
        String xml = MarkLogic.get(uri);
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        if (version.isPresent())
            throw new NoDocumentException(error);
        if (error.statusCode != 307)
            throw new NoDocumentException(error);
        if (!error.header.name.equals("Location"))
            throw new NoDocumentException(error);
        Pattern pattern = Pattern.compile("/([^/]+)/revision$");
        Matcher matcher = pattern.matcher(error.header.value);
        if (!matcher.find())
            throw new RuntimeException();
        version = Optional.of(matcher.group(1));
        return getOrRedirect(query, version);
    }

}
