package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static uk.gov.legislation.data.virtuoso.jsonld.Helper.oneOrMany;

public class Item {

    @JsonProperty("uri")
    @JsonAlias("@id")
    public URI id;

    @JsonProperty("type")
    @JsonAlias("@type")
    public List<String> type;

    @JsonProperty
    public int year;

    @JsonProperty
    public URI calendarYear;

    @JsonProperty
    public URI session;

    @JsonProperty
    public Integer number;

    @JsonProperty
    public List<ValueAndLanguage> title;

    @JsonSetter("title")
    public void setTitle(JsonNode node) {
        this.title = oneOrMany(node, ValueAndLanguage.class);
    }

    @JsonProperty
    public List<ValueAndLanguage> citation;

    @JsonSetter("citation")
    public void setCitation(JsonNode node) {
        this.citation = oneOrMany(node, ValueAndLanguage.class);
    }

    @JsonProperty
    public List<ValueAndLanguage> fullCitation;

    @JsonSetter("fullCitation")
    public void setFullCitation(JsonNode node) {
        this.fullCitation = oneOrMany(node, ValueAndLanguage.class);
    }

    @JsonProperty
    public List<ValueAndLanguage> commentaryCitation;

    @JsonSetter("commentaryCitation")
    public void setCommentaryCitation(JsonNode node) {
        this.commentaryCitation = oneOrMany(node, ValueAndLanguage.class);
    }

    @JsonProperty
    public URI within;

    @JsonProperty
    public List<URI> contains;

    @JsonProperty
    public List<URI> interpretation;

    @JsonProperty
    public List<URI> originalLanguageOfText;

    @JsonSetter("originalLanguageOfText")
    public void setOriginalLanguageOfText(JsonNode node) {
        this.originalLanguageOfText = oneOrMany(node, URI.class);
    }

    @JsonProperty
    public List<String> originalLanguageOfTextIsoCode;

    @JsonSetter("originalLanguageOfTextIsoCode")
    public void setOriginalLanguageOfTextIsoCode(JsonNode node) {
        if (node instanceof TextNode) {
            this.originalLanguageOfTextIsoCode = Collections.singletonList(ValueAndType.convert(node).value);
        } else {
            this.originalLanguageOfTextIsoCode = oneOrMany(node, ValueAndType.class)
                .stream().map(vt -> vt.value).toList();
        }
    }

    public static Item convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, Item.class);
    }

}
