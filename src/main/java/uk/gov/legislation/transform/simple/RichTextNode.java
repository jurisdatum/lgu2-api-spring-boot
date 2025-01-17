package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RichTextNode {

    public static final String TEXT_TYPE = "text";
    public static final String SECTION_TYPE = "section";

    @JacksonXmlProperty(isAttribute = true)
    public String type;

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
