package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValueAndType {

    @JsonProperty("@value")
    public String value;

    @JsonProperty("@type")
    public String type;

}
