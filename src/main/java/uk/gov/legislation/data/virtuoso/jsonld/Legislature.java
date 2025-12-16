package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;

public class Legislature {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
    public URI type;

    public static Legislature convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Legislature.class);
    }

}
