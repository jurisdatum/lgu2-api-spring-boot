package uk.gov.legislation.data.marklogic.legislation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.MarkLogic;
import tools.jackson.core.JacksonException;
import uk.gov.legislation.exceptions.DocumentFetchException;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.exceptions.NoDocumentException;
import uk.gov.legislation.util.Links;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Optional;

@Repository
public class Legislation {

    private static final String ENDPOINT = "legislation.xq";

    private final MarkLogic db;

    public Legislation(MarkLogic db) {
        this.db = db;
    }

    public Response getDocument(String type, String year, int number, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .lang(language);
        return getAndFollowRedirect(params);
    }

    public StreamResponse getDocumentStream(String type, String year, int number, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .lang(language);
        return getAndFollowRedirect2(params);
    }

    /**
     * Get table of contents
     */

    private static final Optional<String> CONTENTS_VIEW = Optional.of("contents");

    private static final Optional<String> METADATA_VIEW = Optional.of("metadata");

    public Response getTableOfContents(String type, String year, int number, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .view(CONTENTS_VIEW)
            .lang(language);
        return getAndFollowRedirect(params);
    }

    public StreamResponse getTableOfContentsStream(String type, String year, int number, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .view(CONTENTS_VIEW)
            .lang(language);
        return getAndFollowRedirect2(params);
    }

    /**
     * Get document section
     */
    public Response getDocumentSection(String type, String year, int number, String section, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .section(Optional.of(section))
            .lang(language);
        return getAndFollowRedirect(params);
    }

    public StreamResponse getDocumentSectionStream(String type, String year, int number, String section, Optional<String> version, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .version(version)
            .section(Optional.of(section))
            .lang(language);
        return getAndFollowRedirect2(params);
    }

    public Response getMetadata(String type, String year, int number, Optional<String> language) {
        Parameters params = new Parameters(type, year, number)
            .view(METADATA_VIEW)
            .lang(language);
        return getAndFollowRedirect(params);
    }

    /* records for return values */

    public record Response(String clml, Optional<Redirect> redirect) {
    }

    public record StreamResponse(InputStream clml, Optional<Redirect> redirect) {
    }

    public record Redirect(String type, String year, long number, Optional<String> version) {

        static Redirect make(Parameters params) {
            return new Redirect(params.type(), params.year(), params.number(), params.version());
        }

        static Optional<Redirect> make(boolean afterRedirect, Parameters params) {
            if (afterRedirect)
                return Optional.of(make(params));
            return Optional.empty();
        }

    }

    /* helper methods */

    private final Logger logger = LoggerFactory.getLogger(Legislation.class);

    private Response getAndFollowRedirect(Parameters params) {
        return getAndFollowRedirect(params, false);
    }

    private Response getAndFollowRedirect(Parameters params, boolean afterRedirect) {
        String xml;
        try {
            xml = db.get(ENDPOINT, params.buildQuery());
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O exception", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Failed to fetch document due to interruption", e);
        }

        Error.RootClassification classification = Error.classifyRoot(xml);
        if (classification == Error.RootClassification.MALFORMED)
            throw new MarkLogicRequestException("MarkLogic response is not well-formed XML");
        if (classification == Error.RootClassification.OTHER) {
            Optional<Redirect> redirect = Redirect.make(afterRedirect, params);
            return new Response(xml, redirect);
        }
        Error error;
        try {
            error = Error.parse(xml);
        } catch (JacksonException e) {
            throw new MarkLogicRequestException("Failed to parse MarkLogic <error> payload", e);
        }
        return getAndFollowRedirect(buildRedirectParams(error, params), true);
    }

    private Parameters buildRedirectParams(Error error, Parameters oldParams) {
        if (error.statusCode >= 400)
            throw new NoDocumentException(error);  // don't use error.toString()
        String location = extractRedirectLocation(error);
        logger.debug("MarkLogic redirecting to {}", location);
        Links.Components comp = Links.parse(location);
        if (comp == null)
            throw new IllegalStateException("Invalid redirect location: " + location);
        return new Parameters(comp.type(), comp.year(), comp.number())
            .version(comp.version())
            .view(oldParams.view())
            .section(comp.fragment().map(fragment -> fragment.replace('/', '-')))
            .lang(comp.language());
    }

    private static String extractRedirectLocation(Error error) {
        if (error.statusCode < 300)
            throw new MarkLogicRequestException("Expected redirect status (3xx) but got " + error.statusCode);
        if (error.header == null || !"Location".equals(error.header.name))
            throw new MarkLogicRequestException("MarkLogic redirect <header> missing 'Location' name");
        String location = error.header.value;
        if (location == null || location.isEmpty())
            throw new MarkLogicRequestException("MarkLogic redirect Location header is empty");
        return location;
    }

    private StreamResponse getAndFollowRedirect2(Parameters params) {
        return getAndFollowRedirect2(params, false);
    }

    private StreamResponse getAndFollowRedirect2(Parameters params, boolean afterRedirect) {
        PushbackInputStream stream = tryOpenStream(params);
        return handleStream(stream, params, afterRedirect);
    }

    private PushbackInputStream tryOpenStream(Parameters params) {
        try {
            return db.getStream(ENDPOINT, params.buildQuery());
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O exception", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DocumentFetchException("Failed to fetch document due to interruption", e);
        }
    }

    private StreamResponse handleStream(PushbackInputStream stream, Parameters params, boolean afterRedirect) {
        boolean keepOpen = false;
        try {
            // classifyRoot on a PushbackInputStream never returns MALFORMED (see RootClassification),
            // so the only non-OTHER result is ERROR.
            Error.RootClassification classification = Error.classifyRoot(stream);
            if (classification == Error.RootClassification.OTHER) {
                keepOpen = true;
                return new StreamResponse(stream, Redirect.make(afterRedirect, params));
            }
            Error error;
            try {
                error = Error.parseAssumingError(stream);
            } catch (IOException e) {
                throw new MarkLogicRequestException("Failed to parse MarkLogic <error> stream", e);
            }
            return getAndFollowRedirect2(buildRedirectParams(error, params), true);
        } catch (IOException e) {
            throw new DocumentFetchException("Failed to fetch document due to I/O exception", e);
        } finally {
            if (!keepOpen) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

}
