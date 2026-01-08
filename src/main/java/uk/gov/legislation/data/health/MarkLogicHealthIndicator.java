package uk.gov.legislation.data.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import uk.gov.legislation.data.marklogic.MarkLogic;


@Component("marklogicStatus")
public class MarkLogicHealthIndicator implements HealthIndicator {

    private static final String ENDPOINT = "ping.xq";
    private final MarkLogic markLogic;

    public MarkLogicHealthIndicator(MarkLogic markLogic) {
        this.markLogic = markLogic;
    }

    @Override
    public Health health() {
        try {
            markLogic.getStatus(ENDPOINT);
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e)
                .build();
        }
    }
}
