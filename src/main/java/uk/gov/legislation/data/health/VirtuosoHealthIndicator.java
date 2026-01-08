package uk.gov.legislation.data.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.Virtuoso;

@Component("virtuosoStatus")
public class VirtuosoHealthIndicator implements HealthIndicator {

    private final ObjectMapper mapper;
    private final Virtuoso virtuoso;

    public VirtuosoHealthIndicator( ObjectMapper mapper, Virtuoso virtuoso) {
        this.mapper = mapper;
        this.virtuoso = virtuoso;
    }

    @Override
    public Health health() {
        try {
            JsonNode root = mapper.readTree(virtuoso.getStatus());
            boolean ok = root.path("boolean").asBoolean(false);

            return ok ? Health.up().build()
                : Health.down().withDetail("reason", "ASK returned false").build();

        } catch (Exception e) {
            return Health.down(e)
                .build();
        }
    }
}
