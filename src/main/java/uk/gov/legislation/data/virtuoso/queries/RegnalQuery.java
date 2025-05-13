package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
import static uk.gov.legislation.data.virtuoso.queries.ReignQuery.*;

@Repository
public class RegnalQuery {

    private final Virtuoso virtuoso;
    private static final String REGNAL_URI_TEMPLATE = "http://www.legislation.gov.uk/id/regnal/%s/%s";

    public RegnalQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    public String getRegnalData(String regnal, Integer regnalYear, String format) throws IOException, InterruptedException {
        String query = buildRegnalQuery(regnal, regnalYear);
        return virtuoso.query(query, format);
    }


    private static String buildRegnalQuery(String regnal, Integer regnalYear) {
        String uri = String.format(RegnalQuery.REGNAL_URI_TEMPLATE, regnal, regnalYear);
        return makeSingleConstructQuery(uri);
    }

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public Optional <Regnal> getRegnalAsJsonLd(String reign, Integer regnalYear) throws IOException, InterruptedException {
        String json = getRegnalData(reign, regnalYear, "application/ld+json");
        ArrayNode graph = Graph.extract(json);

        if(graph == null || graph.isEmpty()) {
            return Optional.empty();
        }

        ObjectNode regnalNode;
        regnalNode = (ObjectNode) graph.get(0);
        processRegnalFields(regnalNode);

        RegnalLD regnalLD = mapper.treeToValue(regnalNode, RegnalLD.class);
        return Optional.of(RegnalConverter.convertToRegnal(regnalLD));
    }

    private void processRegnalFields(ObjectNode node) {

        processUriField(node, "endCalendarYear");
        processUriField(node, "startCalendarYear");
        processUriField(node, "yearOfReign");
        processUriField(node, "reign");

        processDateField(node, "endDate");
        processDateField(node, "startDate");

        processUriArrayField(node, "overlapsCalendarYear");
        processUriArrayField(node, "overlapsRegnalYear");
    }
}



