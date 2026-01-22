package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalYearLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class RegnalYearQuery {

    private final Virtuoso virtuoso;

    public RegnalYearQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    private static String constructQueryForRegnal(String regnal, Integer regnalYear) {
        return makeSingleConstructQuery(String.format("http://www.legislation.gov.uk/id/regnal/%s/%s", regnal, regnalYear));
    }

    public String fetchRawData(String reign, Integer regnalYear, String format)
        throws IOException, InterruptedException {
        return virtuoso.query(constructQueryForRegnal(reign, regnalYear), format);
    }

    public Optional<RegnalYearLD> fetchMappedData(String reign, Integer regnalYear)
        throws IOException, InterruptedException {
        ArrayNode graphNodes = Graph.extract(fetchRawData(reign, regnalYear, "application/ld+json"));

        return Optional.ofNullable(graphNodes)
            .filter(nodes -> !nodes.isEmpty())
            .map(nodes -> nodes.get(0))
//            .filter(JsonNode::isObject)
            .map(ObjectNode.class::cast)
            .map(RegnalYearLD::convert);
    }

}
