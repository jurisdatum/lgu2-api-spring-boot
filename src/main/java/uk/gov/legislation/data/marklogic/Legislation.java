package uk.gov.legislation.data.marklogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.legislation.util.Links;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class Legislation {

    static final String Endpoint = "legislation.xq";

    @Autowired
    private MarkLogic db;

    public String getDocument(String type, int year, int number, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number;
        if (version.isPresent())
            query += "&version=" + URLEncoder.encode(version.get(), StandardCharsets.US_ASCII);
        try {
            return get(query);
        } catch (Redirect e) {
            Links.Components comp = Links.parse(e.location);
            if (comp == null)
                throw new RuntimeException(e);
            return getDocument(comp.type(), comp.year(), comp.number(), comp.version());
        }
    }

    public String getTableOfContents(String type, int year, int number, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number +
            "&view=contents";
        if (version.isPresent())
            query += "&version=" + URLEncoder.encode(version.get(), StandardCharsets.US_ASCII);
        try {
            return get(query);
        } catch (Redirect e) {
            Links.Components comp = Links.parse(e.location);
            if (comp == null)
                throw new RuntimeException(e);
            return getTableOfContents(comp.type(), comp.year(), comp.number(), comp.version());
        }
    }

    public String getDocumentSection(String type, int year, int number, String section, Optional<String> version) throws IOException, InterruptedException, NoDocumentException {
        String query =
            "?type=" + URLEncoder.encode(type, StandardCharsets.US_ASCII) +
            "&year=" + year +
            "&number=" + number +
            "&section=" + URLEncoder.encode(section, StandardCharsets.US_ASCII);
        if (version.isPresent())
            query += "&version=" + URLEncoder.encode(version.get(), StandardCharsets.US_ASCII);
        try {
            return get(query);
        } catch (Redirect e) {
            Links.Components comp = Links.parse(e.location);
            if (comp == null)
                throw new RuntimeException(e);
            if (comp.fragment().isEmpty())
                throw new RuntimeException(e);
            return getDocumentSection(comp.type(), comp.year(), comp.number(), comp.fragment().get(), comp.version());
        }
    }

    private String get(String query) throws IOException, InterruptedException, NoDocumentException, Redirect {
        String xml = db.get(Endpoint, query);
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        if (error.statusCode >= 400)
            throw new NoDocumentException(error);
        if (!error.header.name.equals("Location"))
            throw new RuntimeException(xml);
        throw new Redirect(error.header.value);
    }

    private static class Redirect extends Exception {

        private final String location;

        private Redirect(String location) {
            super(location);
            this.location = location;
        }

    }

}
