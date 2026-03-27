package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static uk.gov.legislation.data.virtuoso.jsonld.Helper.oneOrMany;

public class ItemLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
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
    public List<String> interpretation;

    @JsonProperty
    public List<String> originalLanguageOfText;

    @JsonSetter("originalLanguageOfText")
    public void setOriginalLanguageOfText(JsonNode node) {
        this.originalLanguageOfText = oneOrMany(node, String.class);
    }

    @JsonProperty
    public List<ValueAndType> originalLanguageOfTextIsoCode;

    @JsonSetter("originalLanguageOfTextIsoCode")
    public void setOriginalLanguageOfTextIsoCode(JsonNode node) {
        if (node != null && node.isTextual()) {
            this.originalLanguageOfTextIsoCode = Collections.singletonList(ValueAndType.convert(node));
        } else {
            this.originalLanguageOfTextIsoCode = oneOrMany(node, ValueAndType.class);
        }
    }

    public static ItemLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, ItemLD.class);
    }

}
