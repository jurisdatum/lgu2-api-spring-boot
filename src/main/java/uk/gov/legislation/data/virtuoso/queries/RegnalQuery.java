package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Regnal;
import uk.gov.legislation.converters.ld.RegnalConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class RegnalQuery {

    private final Virtuoso virtuoso;
    private static final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public RegnalQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }
    private static String constructQueryForRegnal(String regnal, Integer regnalYear) {
        return makeSingleConstructQuery(String.format("http://www.legislation.gov.uk/id/regnal/%s/%s", regnal, regnalYear));
    }

    public String fetchRawData(String regnal, Integer regnalYear, String format)
        throws IOException, InterruptedException {
        return virtuoso.query(constructQueryForRegnal(regnal, regnalYear), format);
    }

    public Optional<Regnal> fetchMappedData(String reign, Integer regnalYear)
        throws IOException, InterruptedException {
        ArrayNode graphNodes = Graph.extract(fetchRawData(reign, regnalYear, "application/ld+json"));

        return Optional.ofNullable(graphNodes)
            .filter(nodes -> !nodes.isEmpty())
            .map(nodes -> nodes.get(0))
            .filter(JsonNode::isObject)
            .map(ObjectNode.class::cast)
            .map(node -> {
                try {
                    return mapper.treeToValue(node, RegnalLD.class);
                }
                catch(JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            })
            .map(RegnalConverter::convert);
    }
}




