package uk.gov.legislation.endpoints.health;

import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.marklogic.Ping;
import uk.gov.legislation.data.virtuoso.queries.AskNothing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final BooleanSupplier markLogic;
    private final BooleanSupplier virtuoso;

    public HealthController(Ping markLogic, AskNothing virtuoso) {
        this.markLogic = markLogic;
        this.virtuoso = virtuoso;
    }

    private final ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();

    @PreDestroy
    public void shutdownExecutor() {
        exec.shutdown();
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> body = Map.of("status", "ok");
        return ResponseEntity.ok(body);
    }

    /**
     * Checks both database dependencies in parallel and returns their status.
     *
     * <p>Timeouts are defined to ensure this endpoint responds quickly even
     * when databases are unresponsive:
     * <ul>
     *   <li>HTTP connect timeout: 300ms (in MarkLogic/Virtuoso classes)</li>
     *   <li>HTTP request timeout: 800ms (in MarkLogic/Virtuoso classes)</li>
     *   <li>Overall timeout per check: 1 second (here)</li>
     * </ul>
     */
    @GetMapping("/dependencies")
    public ResponseEntity<Map<String,Object>> dependencies() {

        CompletableFuture<Boolean> marklogicF =
            CompletableFuture.supplyAsync(markLogic::getAsBoolean, exec)
                .completeOnTimeout(false, 1, TimeUnit.SECONDS)
                .exceptionally(e -> false);
        CompletableFuture<Boolean> virtuosoF =
            CompletableFuture.supplyAsync(virtuoso::getAsBoolean, exec)
                .completeOnTimeout(false, 1, TimeUnit.SECONDS)
                .exceptionally(e -> false);

        boolean marklogicOk = marklogicF.join();
        boolean virtuosoOk  = virtuosoF.join();

        Map<String,String> deps = new LinkedHashMap<>();
        deps.put("marklogic", marklogicOk ? "ok" : "fail");
        deps.put("virtuoso", virtuosoOk ? "ok" : "fail");
        boolean allOk = marklogicOk && virtuosoOk;
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", allOk ? "ok" : "fail");
        result.put("dependencies", deps);
        return ResponseEntity
            .status(allOk ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
            .body(result);
    }

}
