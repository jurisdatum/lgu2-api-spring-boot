package uk.gov.legislation.data.virtuoso.model2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Item {

    @JsonProperty
    public String uri;

    @JsonProperty
    public String type;

    @JsonProperty
    public int year;

    @JsonProperty
    public int number;

    @JsonProperty
    public String title;

    @JsonProperty
    public String welshTitle;

    @JsonProperty
    public String citation;

    @JsonProperty
    public String fullCitation;

    @JsonProperty
    public String commentaryCitation;

    @JsonProperty
    public String welshCitation;

    @JsonProperty
    public String welshFullCitation;

    @JsonProperty
    public String welshCommentaryCitation;

    @JsonProperty
    public List<String> originalLanguages;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> interpretations;

}
