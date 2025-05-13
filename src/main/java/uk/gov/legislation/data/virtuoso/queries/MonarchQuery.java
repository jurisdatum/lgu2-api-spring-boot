package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Monarch;
import uk.gov.legislation.converters.ld.MonarchConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.MonarchLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

  @Repository
  public class MonarchQuery {

    private final Virtuoso virtuoso;
    private static final String MONARCH_URI_TEMPLATE = "http://www.legislation.gov.uk/id/monarch/%s";

    public MonarchQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    public String getMonarchData(String monarchName, String format) throws IOException, InterruptedException {
        String query = buildMonarchQuery(monarchName);
        return virtuoso.query(query, format);
    }

    public Optional<Monarch> getMonarchAsJsonLd(String monarchName) throws IOException, InterruptedException {
        String json = getMonarchData(monarchName, "application/ld+json");
        ArrayNode graph = Graph.extract(json);

        if (graph == null || graph.isEmpty()) {
            return Optional.empty();
        }

        ObjectNode monarchNode = (ObjectNode) graph.get(0);
        MonarchLD monarchLD = MonarchLD.fromJsonNode(monarchNode);
        return Optional.of(MonarchConverter.convert(monarchLD));
    }

    private static String buildMonarchQuery(String monarchName) {
        String uri = String.format(MONARCH_URI_TEMPLATE, monarchName);
        return makeSingleConstructQuery(uri);
    }

}
