package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ItemLD {

    @JsonProperty("@id")
    public String id;

    @JsonProperty("@type")
    public List<String> type;

    @JsonProperty
    public int year;

    @JsonProperty
    public String calendarYear;

    @JsonProperty
    public int number;

    @JsonProperty
    public List<ValueAndLanguage> title;

    @JsonProperty
    public List<ValueAndLanguage> citation;

    @JsonProperty
    public List<ValueAndLanguage> fullCitation;

    @JsonProperty
    public List<ValueAndLanguage> commentaryCitation;

    @JsonProperty
    public List<String> interpretation;

    @JsonProperty
    public List<String> originalLanguageOfText;

    @JsonProperty
    public List<ValueAndType> originalLanguageOfTextIsoCode;

    public static ItemLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, ItemLD.class);
    }

}
