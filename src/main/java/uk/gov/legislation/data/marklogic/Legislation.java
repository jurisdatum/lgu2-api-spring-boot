package uk.gov.legislation.data.marklogic;

import org.springframework.stereotype.Service;
import uk.gov.legislation.exceptions.DocumentFetchException;
import uk.gov.legislation.util.Links;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

@Service
public class Legislation {

    private static final String ENDPOINT = "legislation.xq";

    private final MarkLogic db;

    public Legislation(MarkLogic db) {
        this.db = db;
    }


    public String getDocument(String type, int year, int number, Optional<String> version) {
        return handleRequest(
                buildQuery(type, year, number, version, Optional.empty(), Optional.empty()),
                comp -> getDocument(comp.type(), comp.year(), comp.number(), comp.version())
        );
    }

    /** Get table of contents
     */

    public String getTableOfContents(String type, int year, int number, Optional<String> version) {
        return handleRequest(
                buildQuery(type, year, number, version, Optional.of("contents"), Optional.empty()),
                comp -> getTableOfContents(comp.type(), comp.year(), comp.number(), comp.version())
        );
    }

    /** Get document section
     */
    public String getDocumentSection(String type, int year, int number, String section, Optional<String> version) {
        return handleRequest(
                buildQuery(type, year, number, version, Optional.empty(), Optional.of(section)),
                comp -> {
                    if (comp.fragment().isEmpty()) {
                        throw new IllegalStateException("Invalid redirect without fragment.");
                    }
                    return getDocumentSection(comp.type(), comp.year(), comp.number(), comp.fragment().get(), comp.version());
                }
        );
    }

    /** Helper method for handling requests
     */

    private String handleRequest(String query, Function<Links.Components, String> redirectHandler) {
        try {
            return get(query);
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Failed to fetch document due to interruption", e);
        } catch (RedirectException redirect) {
            Links.Components components = Links.parse(redirect.getLocation());
            if (components == null) {
                throw new IllegalStateException("Invalid redirect location: " + redirect.getLocation(), redirect);
            }
            return redirectHandler.apply(components);
        }
    }

    /** Build the query string for requests
    */

     private String buildQuery(String type, int year, int number, Optional<String> version, Optional<String> view, Optional<String> section) {
        StringBuilder query = new StringBuilder();
        query.append("?type=").append(encode(type))
                .append("&year=").append(year)
                .append("&number=").append(number);

        version.ifPresent(v -> query.append("&version=").append(encode(v)));
        view.ifPresent(v -> query.append("&view=").append(encode(v)));
        section.ifPresent(s -> query.append("&section=").append(encode(s)));

        return query.toString();
    }

    /** Fetch document and handle redirection
     * @param query the query string compoent of the MarkLogic request URL
     */
    private String get(String query)
            throws IOException, InterruptedException, NoDocumentException, RedirectException {
        String xml = db.get(ENDPOINT, query);
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        if (error.statusCode >= 400)
            throw new NoDocumentException(error.toString());
        if (!error.header.name.equals("Location"))
            throw new IOException(xml);
        throw new RedirectException(error.header.value);
    }

    /** URL encoding
     * @param value a query parameter
     * @return the URL-encoded parameter
     */
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

}
