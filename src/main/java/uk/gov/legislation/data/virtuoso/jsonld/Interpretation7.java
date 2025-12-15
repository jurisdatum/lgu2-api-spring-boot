package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class Interpretation7 {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public List<URI> type;

    @JsonProperty
    public String interpretationOf;

    @JsonProperty
    public URI languageOfText;

    @JsonProperty
    @JsonDeserialize(using = ValueAndType.DefensiveStringDeserializer.class)
    public String languageOfTextIsoCode;

    @JsonProperty
    public ValueAndLanguage longTitle;

    @JsonProperty
    public ValueAndLanguage shortTitle;

    @JsonProperty
    public ValueAndLanguage orderTitle;

    @JsonProperty
    public ValueAndLanguage statuteTitle;

    @JsonProperty
    public ValueAndLanguage statuteTitleAbbreviated;

    @JsonProperty
    public ValueAndLanguage alternativeStatuteTitle;

    @JsonProperty
    public ValueAndLanguage europeanUnionTitle;

    @JsonProperty
    public ValueAndLanguage subjectDescription;

    @JsonProperty
    public URI within;

    @JsonProperty
    public List<URI> contains;

    public static Interpretation7 convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Interpretation7.class);
    }

}
