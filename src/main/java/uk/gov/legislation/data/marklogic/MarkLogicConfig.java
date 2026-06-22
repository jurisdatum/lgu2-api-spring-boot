package uk.gov.legislation.data.marklogic;

/** MarkLogic connection settings. Plain record — no framework dependency. */
public record MarkLogicConfig(String host, int port, String username, String password) {}
