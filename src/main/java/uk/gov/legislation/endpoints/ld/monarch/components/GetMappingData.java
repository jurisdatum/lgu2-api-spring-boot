package uk.gov.legislation.endpoints.ld.monarch.components;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;
import uk.gov.legislation.endpoints.ld.monarch.response.Monarch;
import uk.gov.legislation.endpoints.ld.monarch.response.MonarchLD;

import java.util.Optional;


public interface GetMappingData {

    Optional<Monarch> apply(String monarchName) throws Exception;

    @Component
    class Default implements GetMappingData {
        private final GetData getData;

        public Default(GetData getData) {
            this.getData = getData;
        }

        @Override
        public Optional<Monarch> apply(String monarchName) throws Exception {
            String json = getData.apply(monarchName, "application/ld+json");
            ArrayNode graph = Graph.extract(json);

            if (graph == null || graph.isEmpty()) return Optional.empty();

            ObjectNode node = (ObjectNode) graph.get(0);
            MonarchLD monarchLD = Graph.mapper.convertValue(node, MonarchLD.class);
            return Optional.of(Converter.DEFAULT.apply(monarchLD));
        }

    }

}

