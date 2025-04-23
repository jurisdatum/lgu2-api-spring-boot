package uk.gov.legislation.api.responses.ld;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String session;

    @JsonProperty
    public int number;

    @JsonProperty
    public String title;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String welshTitle;

    @JsonProperty
    public String citation;

    @JsonProperty
    public String fullCitation;

    @JsonProperty
    public String commentaryCitation;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String welshCitation;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String welshFullCitation;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String welshCommentaryCitation;

    @JsonProperty
    public List<String> originalLanguages;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> interpretations;

}
