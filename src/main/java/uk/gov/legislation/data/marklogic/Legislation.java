package uk.gov.legislation.data.marklogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.exceptions.DocumentFetchException;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.exceptions.NoDocumentException;
import uk.gov.legislation.util.Links;

import java.io.IOException;
import java.util.Optional;

@Repository
public class Legislation {

    private static final String ENDPOINT = "legislation.xq";

    private final MarkLogic db;

    public Legislation(MarkLogic db) {
        this.db = db;
    }

    public Response getDocument(String type, String year, int number, Optional<String> version, Optional<String> language) {
        LegislationParameters params = new LegislationParameters(type, year, number)
                .version(version)
                .lang(language);
        return getAndFollowRedirect(params);
    }

    /** Get table of contents
     */

    private static final Optional<String> CONTENTS_VIEW = Optional.of("contents");

    public Response getTableOfContents(String type, String year, int number, Optional<String> version, Optional<String> language) {
        LegislationParameters params = new LegislationParameters(type, year, number)
                .version(version)
                .view(CONTENTS_VIEW)
                .lang(language);
        return getAndFollowRedirect(params);
    }

    /** Get document section
     */
    public Response getDocumentSection(String type, String year, int number, String section, Optional<String> version, Optional<String> language) {
        LegislationParameters params = new LegislationParameters(type, year, number)
                .version(version)
                .section(Optional.of(section))
                .lang(language);
        return getAndFollowRedirect(params);
    }

    /* records for return values */

    public record Response(String clml, Optional<Redirect> redirect) { }

    public record Redirect(String type, String year, int number, Optional<String> version) {

        static Redirect make(LegislationParameters params) {
            return new Redirect(params.type(), params.year(), params.number(), params.version());
        }

        static Optional<Redirect> make(boolean afterRedirect, LegislationParameters params) {
            if (afterRedirect)
                return Optional.of(make(params));
            return Optional.empty();
        }

    }

    /* helper methods */

    private final Logger logger = LoggerFactory.getLogger(Legislation.class);

    private Response getAndFollowRedirect(LegislationParameters params) {
        return getAndFollowRedirect(params, false);
    }

    private Response getAndFollowRedirect(LegislationParameters params, boolean afterRedirect) {
        String xml;
        try {
            xml = db.get(ENDPOINT, params.buildQuery());
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O exception", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Failed to fetch document due to interruption", e);
        }

        // test to see whether the XML is an <error> response
        Error error;
        try {
            error = Error.parse(xml);
        } catch (Exception e) {
            // the XML was not an <error> response, so it's good CLML; return it
            Optional<Redirect> redirect = Redirect.make(afterRedirect, params);
            return new Response(xml, redirect);
        }
        // the response from MarkLogic was an <error>, so try to follow the redirect
        return handleError(error, params);
    }

    private Response handleError(Error error, LegislationParameters oldParams) {
        if (error.statusCode >= 400)
            throw new NoDocumentException(error);  // don't use error.toString()
        if (!error.header.name.equals("Location"))
            throw new MarkLogicRequestException("Error parsing MarkLogic error response");
        String location = error.header.value;
        logger.debug("MarkLogic redirecting to {}", location);
        Links.Components comp = Links.parse(location);
        if (comp == null)
            throw new IllegalStateException("Invalid redirect location: " + location);
        LegislationParameters newParams = new LegislationParameters(comp.type(), comp.year(), comp.number())
            .version(comp.version())
            .view(oldParams.view())
            .section(comp.fragment().map(fragment -> fragment.replace('/', '-')))
            .lang(comp.language());
        return getAndFollowRedirect(newParams, true);
    }

}
