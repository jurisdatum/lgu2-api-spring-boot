package uk.gov.legislation.endpoints.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.Ping;

/**
 * Spring Boot Actuator health indicator for MarkLogic.
 *
 * <p>Retained for use when Actuator web endpoints are enabled, or for
 * other Actuator features such as metrics.
 */
@Component("marklogicStatus")
public class MarkLogicIndicator implements HealthIndicator {

    private final Ping markLogic;

    public MarkLogicIndicator(Ping markLogic) {
        this.markLogic = markLogic;
    }

    @Override
    public Health health() {
        return markLogic.getAsBoolean() ? Health.up().build() : Health.down().build();
    }

}
