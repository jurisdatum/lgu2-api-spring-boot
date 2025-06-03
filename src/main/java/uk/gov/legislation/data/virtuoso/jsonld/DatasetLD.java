package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public class DatasetLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public List<URI> types;

    @JsonProperty
    public ValueAndType created;

    @JsonProperty
    public ValueAndType modified;

    @JsonProperty
    public String label;

    @JsonProperty
    public String title;

}
