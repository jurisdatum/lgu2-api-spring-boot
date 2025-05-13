package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class InterpretationLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public List<String> type;

    @JsonProperty
    public String interpretationOf;

    @JsonProperty
    public String languageOfText;

    @JsonProperty
    public ValueAndType languageOfTextIsoCode;

    @JsonSetter("languageOfTextIsoCode")
    public void setLanguageOfTextIsoCode(JsonNode node) {
        languageOfTextIsoCode = ValueAndType.convert(node);
    }

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

    public static InterpretationLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, InterpretationLD.class);
    }

}
