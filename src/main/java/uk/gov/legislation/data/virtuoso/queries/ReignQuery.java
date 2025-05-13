package uk.gov.legislation.data.virtuoso.queries;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.api.responses.ld.Reign;
import uk.gov.legislation.converters.ld.ReignConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.data.virtuoso.jsonld.ReignLD;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

@Repository
public class ReignQuery {

    private final Virtuoso virtuoso;
    private static final String REIGN_URI_TEMPLATE = "http://www.legislation.gov.uk/id/reign/%s";

    public ReignQuery(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    public String getReignData(String reign, String format) throws IOException, InterruptedException {
        String query = buildReignQuery(reign);
        return virtuoso.query(query, format);
    }


    private static String buildReignQuery(String reign) {
        String uri = String.format(ReignQuery.REIGN_URI_TEMPLATE, reign);
        return makeSingleConstructQuery(uri);
    }
    private static final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public Optional<Reign> getReignAsJsonLd(String reignId) throws IOException, InterruptedException {
        String json = getReignData(reignId, "application/ld+json");
        ArrayNode graph = Graph.extract(json);

        if (graph == null || graph.isEmpty()) {
            return Optional.empty();
        }

        ObjectNode reignNode = (ObjectNode) graph.get(0);
        processReignFields(reignNode);

        ReignLD reignLD = mapper.treeToValue(reignNode, ReignLD.class);
        return Optional.of(ReignConverter.convertToReign(reignLD));
    }

    private void processReignFields(ObjectNode node) {

        processUriField(node, "endCalendarYear");
        processUriField(node, "endRegnalYear");
        processUriField(node, "startCalendarYear");
        processUriField(node, "startRegnalYear");
        processUriField(node, "monarch");

        processDateField(node, "endDate");
        processDateField(node, "startDate");

        processUriArrayField(node, "overlapsCalendarYear");
        processUriArrayField(node, "overlapsRegnalYear");
    }

    public static void processUriField(ObjectNode node, String fieldName) {
        if (node.has(fieldName)) {
            JsonNode valueNode = node.get(fieldName);
            if (valueNode.isTextual()) {
                String uri = valueNode.asText();
                node.put(fieldName, extractValueFromUri(uri));
            }
        }
    }

    public static void processDateField(ObjectNode node, String fieldName) {
        if (node.has(fieldName)) {
            JsonNode valueNode = node.get(fieldName);
            if (valueNode.isTextual()) {
                String uri = valueNode.asText();
                String dateStr = uri.substring(uri.lastIndexOf('/') + 1);
                node.put(fieldName, dateStr);
            }
        }
    }

    public static void processUriArrayField(ObjectNode node, String fieldName) {
        if (node.has(fieldName)) {
            ArrayNode newArray = mapper.createArrayNode();
            JsonNode arrayNode = node.get(fieldName);

            if (arrayNode.isArray()) {
                arrayNode.forEach(item -> {
                    if (item.isTextual()) {
                        String uri = item.asText();
                        newArray.add(extractValueFromUri(uri));
                    }
                });
            } else if (arrayNode.isTextual()) {
                String uri = arrayNode.asText();
                newArray.add(extractValueFromUri(uri));
            }

            node.set(fieldName, newArray);
        }
    }

    public static String extractValueFromUri(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }}


