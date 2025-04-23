package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class InterpretationLD {

    @JsonProperty("@id")
    public String id;

    @JsonProperty("@type")
    public List<String> type;

    @JsonProperty
    public String interpretationOf;

    @JsonProperty
    public String languageOfText;

    @JsonProperty
    public ValueAndType languageOfTextIsoCode;

    @JsonProperty
    public ValueAndLanguage longTitle;

    @JsonProperty
    public ValueAndLanguage shortTitle;

    @JsonProperty
    public ValueAndLanguage orderTitle;

    public static InterpretationLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, InterpretationLD.class);
    }

}
