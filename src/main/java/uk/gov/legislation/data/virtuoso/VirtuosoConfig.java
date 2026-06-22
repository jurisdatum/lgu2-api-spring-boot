package uk.gov.legislation.data.virtuoso;

import java.util.Objects;

/** Virtuoso connection settings. Plain record — no framework dependency. */
public record VirtuosoConfig(String host, int port) {
    public VirtuosoConfig {
        // Preserves Virtuoso's old fail-fast, framework-agnostic so it also holds outside Spring.
        Objects.requireNonNull(host, "VIRTUOSO_HOST must not be null");
    }
}
