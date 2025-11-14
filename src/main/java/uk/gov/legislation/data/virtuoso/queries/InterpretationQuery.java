package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.converters.ld.InterpretationConverter;
import uk.gov.legislation.converters.ld.ItemConverter;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.Interpretation8LD;
import uk.gov.legislation.data.virtuoso.jsonld.InterpretationLD;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class InterpretationQuery {

    private static final Logger log = LoggerFactory.getLogger(InterpretationQuery.class);

    private final Virtuoso virtuoso;

    public InterpretationQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    /**
     * @param type    the first component of the URI, cannot be null
     * @param middle  the following components of the URI, can contain slashes, cannot be null
     * @param number  can be null
     * @param version can be null
     * @param welsh   whether to append /welsh
     * @return the SPARQL query string
     */
    String makeSparqlQuery(String type, String middle, String number, String version, boolean welsh) {
        String workUri = "http://www.legislation.gov.uk/id/%s/%s".formatted(type, middle);
        String exprUri = "http://www.legislation.gov.uk/%s/%s".formatted(type, middle);
        if (number != null) {
            workUri += "/" + number;
            exprUri += "/" + number;
        }
        if (version != null)
            exprUri += "/" + version;
        if (welsh)
            exprUri += "/welsh";
        return """
            CONSTRUCT { ?s ?p ?o . }
            WHERE { VALUES ?s { <%s> <%s> } ?s ?p ?o . }
            """.formatted(workUri, exprUri);
    }

    public String get(String type, String middle, String number, String version, boolean welsh, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, middle, number, version, welsh);
        return virtuoso.query(query, format);
    }

    public Optional<Interpretation> get(String type, String middle, String number, String version, boolean welsh) throws IOException, InterruptedException {
        String json = get(type, middle, number, version, welsh, "application/ld+json");
        ArrayNode graph = Graph.extract(json);

        if (graph == null || graph.isEmpty())
            return Optional.empty();

        if (graph.size() == 1) {
            // NESTED FORMAT (Virtuoso 8.3.x)
            // Interpretation is the only object, Item is embedded in interpretationOf
            log.debug("Detected nested JSON-LD format (Virtuoso 8.3.x) - Item embedded in interpretationOf");
            ObjectNode interpretationNode = (ObjectNode) graph.get(0);
            Interpretation8LD interpretation8LD = Interpretation8LD.convert(interpretationNode);
            Interpretation interpretation = InterpretationConverter.convert(interpretation8LD);
            return Optional.of(interpretation);

        }

        List<ObjectNode> candidates = StreamSupport.stream(graph.spliterator(), false)
            .filter(ObjectNode.class::isInstance)
            .map(ObjectNode.class::cast)
            .filter(node -> node.hasNonNull("@id"))
            .filter(node -> !node.get("@id").asText().startsWith("_:"))
            .toList();
        List<ObjectNode> interpretationNodes = candidates.stream()
            .filter(node -> node.has("@type"))
            .filter(node -> StreamSupport.stream(node.withArray("@type").spliterator(), false)
                .anyMatch(tpe -> Resources.Leg.Interpretation.equals(tpe.asText())))
            .toList();
        List<ObjectNode> itemNodes = candidates.stream()
            .filter(node -> node.has("@type"))
            .filter(node -> StreamSupport.stream(node.withArray("@type").spliterator(), false)
                .anyMatch(tpe -> Resources.Leg.Item.equals(tpe.asText())))
            .toList();

        if (interpretationNodes.size() != 1 || itemNodes.size() != 1) {
            log.error("Unexpected JSON-LD structure: interpretations={}, items={}",
                interpretationNodes.size(),
                itemNodes.size());
            throw new IllegalStateException("Unexpected Interpretation JSON-LD format");
        }

        // FLAT FORMAT (Virtuoso 7.x)
        // Item and Interpretation are separate array elements
        log.debug("Detected flat JSON-LD format (Virtuoso 7.x) - Item and Interpretation as separate objects");

        ObjectNode itemNode = itemNodes.getFirst();
        ObjectNode interpretationNode = interpretationNodes.getFirst();
        InterpretationLD interpretationLD = InterpretationLD.convert(interpretationNode);
        ItemLD itemLD = ItemLD.convert(itemNode);

        // Convert and combine
        Interpretation interpretation = InterpretationConverter.convert(interpretationLD);
        interpretation.item = ItemConverter.convert(itemLD);
        return Optional.of(interpretation);
    }

}
