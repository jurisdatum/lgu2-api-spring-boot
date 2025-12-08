package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;

import java.net.URI;
import java.util.List;

public class Interpretation8 {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI uri;

    @JsonProperty("type")
    @JsonAlias("@type")
    public List<URI> type;

    @JsonProperty
    public Item interpretationOf;

    @JsonProperty
    public URI languageOfText;

    @JsonProperty
    public String languageOfTextIsoCode;

    @JsonSetter("languageOfTextIsoCode")
    public void setLanguageOfTextIsoCode(JsonNode node) {
        languageOfTextIsoCode = ValueAndType.convert(node).value;
    }

    @JsonProperty
    public ValueAndLanguage shortTitle;

    @JsonProperty
    public ValueAndLanguage longTitle;

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

    ObjectReader reader = Graph.mapper.readerFor(Interpretation8.class);

}
