package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.converters.ld.Interpretation7to8;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.Interpretation8;
import uk.gov.legislation.data.virtuoso.jsonld.Interpretation7;
import uk.gov.legislation.data.virtuoso.jsonld.Item;

import java.io.IOException;
import java.util.Optional;

@Repository
public class InterpretationQuery {

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

    public Optional<Interpretation8> get(String type, String middle, String number, String version, boolean welsh) throws IOException, InterruptedException {
        String json = get(type, middle, number, version, welsh, "application/ld+json");
        ArrayNode graph = Graph.extract(json);
        if (graph == null)
            return Optional.empty();
        if (graph.size() < 2)
            return Optional.empty();
        ObjectNode item0;
        ObjectNode interpretation0;
        ObjectNode o0 = (ObjectNode) graph.get(0);
        TextNode id0 = (TextNode) o0.get("@id");
        String id = id0.asText();
        if (id.contains("/id/")) {
            item0 = o0;
            interpretation0 = (ObjectNode) graph.get(1);
        } else {
            interpretation0 = o0;
            item0 = (ObjectNode) graph.get(1);
        }
        Interpretation7 interpretation1 = Interpretation7.convert(interpretation0);
        Item item1 = Item.convert(item0);
        Interpretation8 interpretation8 = Interpretation7to8.convert(interpretation1, item1);
        return Optional.of(interpretation8);
    }

}
