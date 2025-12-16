package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;

public class Item {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI id;

    @JsonProperty("type")
    @JsonAlias("@type")
    public List<URI> type;

    @JsonProperty
    public int year;

    @JsonProperty
    public URI calendarYear;

    @JsonProperty
    public URI session;

    @JsonProperty
    public Integer number;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ValueAndLanguage> title;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ValueAndLanguage> citation;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ValueAndLanguage> fullCitation;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<ValueAndLanguage> commentaryCitation;

    @JsonProperty
    public URI within;

    @JsonProperty
    public List<URI> contains;

    @JsonProperty
    public List<URI> interpretation;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    public List<URI> originalLanguageOfText;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonDeserialize(contentUsing = ValueAndType.DefensiveStringDeserializer.class)
    public List<String> originalLanguageOfTextIsoCode;

    public static Item convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Item.class);
    }

}
