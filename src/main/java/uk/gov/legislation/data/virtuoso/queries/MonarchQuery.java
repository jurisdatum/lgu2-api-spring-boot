package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.converters.ld.MonarchConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.api.responses.ld.Monarch;
import uk.gov.legislation.data.virtuoso.jsonld.MonarchLD;

import java.io.IOException;
import java.util.Optional;

@Repository
public class MonarchQuery {

    private final Virtuoso virtuoso;

    public MonarchQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    private static String buildMonarchQuery(String monarchName) {
        String uri = String.format("http://www.legislation.gov.uk/id/monarch/%s", monarchName);
        return Query.makeSingleConstructQuery(uri);
    }

    public String fetchRawData(String name, String format) throws IOException, InterruptedException {
        String query = buildMonarchQuery(name);
        return virtuoso.query(query, format);
    }

    public Optional<Monarch> fetchMappedData(String monarchName) throws IOException, InterruptedException {
        String jsonData = fetchRawData(monarchName, "application/ld+json");

        return Optional.ofNullable(Graph.extract(jsonData))
            .filter(graph -> !graph.isEmpty())
            .map(graph -> (ObjectNode) graph.get(0))
            .map(MonarchLD::convert)
            .map(MonarchConverter::convert);
    }

}
