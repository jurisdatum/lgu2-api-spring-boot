package uk.gov.legislation.endpoints.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.queries.AskNothing;

/**
 * Spring Boot Actuator health indicator for Virtuoso.
 *
 * <p>Retained for use when Actuator web endpoints are enabled, or for
 * other Actuator features such as metrics.
 */
@Component("virtuosoStatus")
public class VirtuosoIndicator implements HealthIndicator {

    private final AskNothing virtuoso;

    public VirtuosoIndicator(AskNothing virtuoso) {
        this.virtuoso = virtuoso;
    }

    @Override
    public Health health() {
        return virtuoso.getAsBoolean() ? Health.up().build() : Health.down().build();
    }

}
