package uk.gov.legislation.data.marklogic.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class AtomLink {

    @JacksonXmlProperty(isAttribute = true)
    public String rel;

    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty(isAttribute = true)
    public String href;

    @JacksonXmlProperty(isAttribute = true)
    public String title;

}
