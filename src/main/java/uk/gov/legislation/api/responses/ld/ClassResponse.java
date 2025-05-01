package uk.gov.legislation.api.responses.ld;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = false)
public class ClassResponse {

    @JsonProperty
    public URI uri;

    @JsonAnyGetter
    public Map<String, Object> other;

}
