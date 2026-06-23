package uk.gov.legislation.data.virtuoso.defra;

/** DefraLex connection settings. Plain record — no framework dependency. */
public record DefraLexConfig(String host, int port) {}
