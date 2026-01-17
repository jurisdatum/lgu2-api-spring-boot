package uk.gov.legislation.data.marklogic;

import org.springframework.stereotype.Repository;

import java.util.function.BooleanSupplier;

/**
 * Status check for MarkLogic. Returns true if the database responds
 * successfully within the timeouts defined in MarkLogic, false otherwise.
 */
@Repository
public class Ping implements BooleanSupplier {

    private static final String ENDPOINT = "ping.xq";

    private final MarkLogic markLogic;

    public Ping(MarkLogic markLogic) {
        this.markLogic = markLogic;
    }

    @Override
    public boolean getAsBoolean() {
        try {
            return markLogic.getStatus(ENDPOINT, "") == 200;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
