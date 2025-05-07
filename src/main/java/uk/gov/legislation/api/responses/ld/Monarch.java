package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public record Monarch(
    @JsonProperty URI uri,
    @JsonProperty String type,
    @JsonProperty String label,
    @JsonProperty String name,
    @JsonProperty Integer number
) {}
