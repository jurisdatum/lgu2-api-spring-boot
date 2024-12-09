package uk.gov.legislation.data.marklogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.legislation.exceptions.DocumentFetchException;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.util.Links;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class Legislation {

    private static final String ENDPOINT = "legislation.xq";

    private final MarkLogic db;

    public Legislation(MarkLogic db) {
        this.db = db;
    }

    public Response getDocument(String type, String year, int number, Optional<String> version) {
        Parameters params = new Parameters(type, year, number).version(version);
        return getAndFollowRedirect(params);
    }

    /** Get table of contents
     */

    private static final Optional<String> CONTENTS_VIEW = Optional.of("contents");

    public Response getTableOfContents(String type, String year, int number, Optional<String> version) {
        Parameters params = new Parameters(type, year, number).version(version).view(CONTENTS_VIEW);
        return getAndFollowRedirect(params);
    }

    /** Get document section
     */
    public Response getDocumentSection(String type, String year, int number, String section, Optional<String> version) {
        Parameters params = new Parameters(type, year, number).version(version).section(Optional.of(section));
        return getAndFollowRedirect(params);
    }

    /* helper methods */

    public record Redirect(String type, String year, int number, Optional<String> version) {

        static Redirect make(Parameters params) {
            return new Redirect(params.type(), params.year(), params.number(), params.version());
        }

        static Optional<Redirect> make(boolean afterRedirect, Parameters params) {
            if (afterRedirect)
                return Optional.of(make(params));
            return Optional.empty();
        }

    }

    public record Response(String clml, Optional<Redirect> redirect) { }

    private Response getAndFollowRedirect(Parameters params) {
        return getAndFollowRedirect(params, false);
    }
    private Response getAndFollowRedirect(Parameters params, boolean afterRedirect) {
        try {
            String clml = get(params);
            Optional<Redirect> redirect = Redirect.make(afterRedirect, params);
            return new Response(clml, redirect);
        } catch (RedirectException e) {
            Links.Components comp = Links.parse(e.getLocation());
            if (comp == null)
                throw new IllegalStateException("Invalid redirect location: " + e.getLocation(), e);
            params = new Parameters(comp.type(), comp.year(), comp.number())
                .version(comp.version())
                .view(params.view)
                .section(params.section); // FixMe I believe this should be comp.fragment() ?
            return getAndFollowRedirect(params, true);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(Legislation.class);

    private String get(Parameters params) throws NoDocumentException, RedirectException {
        String xml;
        try {
            xml = db.get(ENDPOINT, params.buildQuery());
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Failed to fetch document due to interruption", e);
        }
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            return xml;
        }
        if (error.statusCode >= 400)
            throw new NoDocumentException(error);
        if (!error.header.name.equals("Location"))
            throw new MarkLogicRequestException("Error parsing MarkLogic error response");
        logger.debug("MarkLogic redirecting to {}", error.header.value);
        throw new RedirectException(error.header.value);
    }

    /* parameters */

    static class Parameters {

        private final String type;

        private final String year;

        private final int number;

        private Optional<String> version;

        private Optional<String> view;

        private Optional<String> section;

        Parameters(String type, String year, int number) {
            if (type == null)
                throw new IllegalArgumentException();
            if (year == null)
                throw new IllegalArgumentException();
            this.type = type;
            this.year = year;
            this.number = number;
            this.version = Optional.empty();
            this.view = Optional.empty();
            this.section = Optional.empty();
        }

        String type() { return type; }

        String year() { return year; }

        int number() { return number; }

        Optional<String> version() { return version; }

        Parameters version(Optional<String> version) {
            this.version = version;
            return this;
        }

        Parameters view(Optional<String> view) {
            this.view = view;
            return this;
        }

        Parameters section(Optional<String> section) {
            this.section = section;
            return this;
        }

        String buildQuery() {
            StringBuilder query = new StringBuilder();
            query.append("?type=").append(encode(type))
                .append("&year=").append(encode(year))
                .append("&number=").append(number);
            version.ifPresent(v -> query.append("&version=").append(encode(v)));
            view.ifPresent(v -> query.append("&view=").append(encode(v)));
            section.ifPresent(s -> query.append("&section=").append(encode(s)));
            return query.toString();
        }

        static String encode(String value) {
            return URLEncoder.encode(value, StandardCharsets.US_ASCII);
        }

    }

}
