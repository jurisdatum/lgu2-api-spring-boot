package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class ItemLD {

    @JsonProperty("@id")
    public String id;

    @JsonProperty("@type")
    public List<String> type;

    @JsonProperty
    public int year;

    @JsonProperty
    public URI calendarYear;

    @JsonProperty
    public URI session;

    @JsonProperty
    public int number;

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
        if (node instanceof TextNode) {
            this.originalLanguageOfTextIsoCode = Collections.singletonList(ValueAndType.convert(node));
        } else {
            this.originalLanguageOfTextIsoCode = oneOrMany(node, ValueAndType.class);
        }
    }

    public static ItemLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, ItemLD.class);
    }

    /* for fields that are sometimes an object and sometimes an array */
    private static <T> List<T> oneOrMany(JsonNode node, Class<T> t) {
        if (node instanceof ArrayNode)
            return StreamSupport.stream(node.spliterator(), false)
                .map(o -> Graph.mapper.convertValue(o, t))
                .toList();
        return Collections.singletonList(
            Graph.mapper.convertValue(node, t)
        );
    }

}
