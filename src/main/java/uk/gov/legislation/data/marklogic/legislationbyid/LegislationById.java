package uk.gov.legislation.data.marklogic.legislationbyid;

import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.exceptions.DocumentFetchException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Repository
public class LegislationById {

    private static final String ENDPOINT = "legislation-by-id.xq";

    private final MarkLogic db;

    public LegislationById(MarkLogic db) {
        this.db = db;
    }

    /**
     * Checks whether a fragment exists in MarkLogic.
     *
     * @param type    document type, e.g. "ukpga"
     * @param year    year string, e.g. "2024" or "Eliz1/2020" for regnal years
     * @param number  document number
     * @param section section identifier using hyphens, e.g. "section-1-1"
     * @return true if the fragment exists, false if it does not
     */
    public boolean exists(String type, String year, int number, String section) {
        String query = "?type=" + encode(type)
            + "&year=" + encode(year)
            + "&number=" + number
            + "&section=" + encode(section);
        String xml;
        try {
            xml = db.get(ENDPOINT, query);
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to check fragment existence", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Fragment existence check interrupted", e);
        }
        try {
            Error error = Error.parse(xml);
            if (error.statusCode == 303)
                return true;
            if (error.statusCode == 404)
                return false;
            throw new DocumentFetchException(
                "Unexpected status code from legislation-by-id.xq: " + error.statusCode);
        } catch (JacksonException e) {
            throw new DocumentFetchException("Failed to parse legislation-by-id response", e);
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

}
