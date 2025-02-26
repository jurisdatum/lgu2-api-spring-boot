package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = RichTextNode.Text.class, name = "text"),
    @JsonSubTypes.Type(value = RichTextNode.Section.class, name = "section"),
    @JsonSubTypes.Type(value = RichTextNode.Range.class, name = "range")
})
@JacksonXmlRootElement(localName = "node")
public abstract class RichTextNode {

    public static class Text extends RichTextNode {

        @JacksonXmlProperty(isAttribute = true)
        public String text;

    }

    public static class Section extends RichTextNode {

        @JacksonXmlProperty(isAttribute = true)
        public String text;

        @JacksonXmlProperty(isAttribute = true)
        public String ref;

        @JacksonXmlProperty(isAttribute = true)
        public String uri;

        @JacksonXmlProperty(isAttribute = true)
        public String error;

        @JacksonXmlProperty(isAttribute = true)
        public boolean missing;

    }

    public static class Range extends RichTextNode {

        @JacksonXmlProperty(isAttribute = true)
        public String start;

        @JacksonXmlProperty(isAttribute = true)
        public String end;

        @JacksonXmlProperty(isAttribute = true)
        public String uri;

        @JacksonXmlProperty(isAttribute = true)
        public String upTo;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "node")
        public List<RichTextNode> children;

    }

}
