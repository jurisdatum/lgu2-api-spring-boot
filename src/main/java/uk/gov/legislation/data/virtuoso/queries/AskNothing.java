package uk.gov.legislation.data.virtuoso.queries;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.virtuoso.Virtuoso;

import java.util.function.BooleanSupplier;

/**
 * Status check for Virtuoso. Executes a trivial SPARQL query (ASK {})
 * and returns true if the database responds successfully within the
 * timeouts defined in Virtuoso, false otherwise.
 */
@Repository
public class AskNothing implements BooleanSupplier {

    private static final String QUERY = "ASK {}";

    private final Virtuoso virtuoso;

    public AskNothing(Virtuoso virtuoso) {
        this.virtuoso = virtuoso;
    }

    @Override
    public boolean getAsBoolean() {
        try {
            return virtuoso.getStatus(QUERY) == 200;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
