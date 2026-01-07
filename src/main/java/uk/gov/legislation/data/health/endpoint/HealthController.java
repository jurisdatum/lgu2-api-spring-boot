package uk.gov.legislation.data.health.endpoint;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.health.MarkLogicHealthIndicator;
import uk.gov.legislation.data.health.VirtuosoHealthIndicator;

import java.util.LinkedHashMap;
import java.util.Map;
@RestController
public class HealthController {

    private final HealthIndicator markLogicHealth;
        private final HealthIndicator virtuosoHealth;
        private final HealthEndpoint healthEndpoint;


        public HealthController(MarkLogicHealthIndicator markLogicHealth,
            VirtuosoHealthIndicator virtuosoHealth, HealthEndpoint healthEndpoint) {
            this.markLogicHealth = markLogicHealth;
            this.virtuosoHealth = virtuosoHealth;
            this.healthEndpoint = healthEndpoint;
        }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        String status = healthEndpoint.health().getStatus().getCode();
        Map<String, String> body = Map.of("status", status.equals("UP") ? "ok" : "fail");
        if (status.equals("UP")) {
            return ResponseEntity.ok(body);
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }


    @GetMapping("/health/dependencies")
        public ResponseEntity <Map <String, Object>> healthDependencies() {
            Map<String, String> dbChecks = new LinkedHashMap <>();
            dbChecks.put("markLogic", markLogicHealth.health().getStatus().getCode().equals("UP") ? "ok" : "fail");
            dbChecks.put("virtuoso", virtuosoHealth.health().getStatus().getCode().equals("UP") ? "ok" : "fail");

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", "ok");
            result.put("dependencies", dbChecks);

            return ResponseEntity.ok(result);
        }
}
