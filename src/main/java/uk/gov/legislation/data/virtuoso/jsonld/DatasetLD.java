package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public class DatasetLD {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
    public List<URI> type;

    @JsonProperty
    public ValueAndType created;

    @JsonProperty
    public ValueAndType modified;

    @JsonProperty
    public String label;

    @JsonProperty
    public String title;

}
