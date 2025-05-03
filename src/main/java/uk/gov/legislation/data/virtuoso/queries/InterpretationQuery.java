package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.converters.ld.InterpretationConverter;
import uk.gov.legislation.converters.ld.ItemConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.InterpretationLD;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;

import java.io.IOException;
import java.util.Optional;

@Repository
public class InterpretationQuery {

    private final Virtuoso virtuoso;

    public InterpretationQuery(Virtuoso virtuoso) { this.virtuoso = virtuoso; }

    String makeSparqlQuery(String type, String year, int number, String version, boolean welsh) {
        String workUri = "http://www.legislation.gov.uk/id/%s/%s/%d".formatted(type, year, number);
        String exprUri = "http://www.legislation.gov.uk/%s/%s/%d".formatted(type, year, number);
        if (version != null)
            exprUri += "/" + version;
        if (welsh)
            exprUri += "/welsh";
        return """
            CONSTRUCT { ?s ?p ?o . }
            WHERE { VALUES ?s { <%s> <%s> } ?s ?p ?o . }
            """.formatted(workUri, exprUri);
    }

    public String get(String type, String year, int number, String version, boolean welsh, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(type, year, number, version, welsh);
        return virtuoso.query(query, format);
    }

    public Optional<Interpretation> get(String type, String year, int number, String version, boolean welsh) throws IOException, InterruptedException {
        String json = get(type, year, number, version, welsh, "application/ld+json");
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
        InterpretationLD interpretation1 = InterpretationLD.convert(interpretation0);
        ItemLD item1 = ItemLD.convert(item0);
        Interpretation interpretation2 = InterpretationConverter.convert(interpretation1);
        interpretation2.item = ItemConverter.convert(item1);
        return Optional.of(interpretation2);
    }

}
