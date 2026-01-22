package uk.gov.legislation.transform.simple;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;

public class TitledThing {

    @JacksonXmlProperty(localName = "IdURI", isAttribute = true)
    public URI uri;

    @JacksonXmlProperty(localName = "title", isAttribute = true)
    public String title;

}
