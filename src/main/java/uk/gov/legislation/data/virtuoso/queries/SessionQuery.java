package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.node.ObjectNode;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.SessionLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class SessionQuery {

    private final Virtuoso virtuoso;

    public SessionQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    private static String constructQuery(String session) {
        String uri = String.format("http://www.legislation.gov.uk/id/session/EnglishParliament/%s", session);
        return makeSingleConstructQuery(uri);
    }

    public String fetchRawData(String session, String format)
        throws IOException, InterruptedException {
        return virtuoso.query(constructQuery(session), format);
    }

    public Optional<SessionLD> fetchMappedData(String session)
        throws IOException, InterruptedException {
        String jsonData = fetchRawData(session, "application/ld+json");
        return toMappedData(jsonData);
    }

    private static String constructQuery(String legislature, String reign, String session) {
        String uri = String.format(
            "http://www.legislation.gov.uk/id/session/%s/%s/%s",
            legislature, reign, session
        );
        return makeSingleConstructQuery(uri);
    }

    public String fetchRawData(String legislature, String reign, String session, String format)
        throws IOException, InterruptedException {
        return virtuoso.query(constructQuery(legislature, reign, session), format);
    }

    public Optional<SessionLD> fetchMappedData(String legislature, String reign, String session)
        throws IOException, InterruptedException {
        String jsonData = fetchRawData(legislature, reign, session,"application/ld+json");
        return toMappedData(jsonData);
    }

    private Optional<SessionLD> toMappedData(String jsonData) throws JacksonException {
        return Optional.ofNullable(Graph.extract(jsonData))
            .filter(graph -> !graph.isEmpty())
            .map(graph -> (ObjectNode) graph.get(0))
            .map(SessionLD::convert);
    }

}
