package uk.gov.legislation.transform.simple;

import java.net.URI;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TitledThing {

    @JacksonXmlProperty(localName = "IdURI", isAttribute = true)
    public URI uri;

    @JacksonXmlProperty(localName = "title", isAttribute = true)
    public String title;
}
