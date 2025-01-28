package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.util.Labels;
import uk.gov.legislation.util.Links;

public class Level {

    @JacksonXmlProperty(isAttribute = true)
    public String name;

    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "DocumentURI", isAttribute = true)
    public String uri;

    @JacksonXmlProperty
    public String number;

    @JacksonXmlProperty
    public String title;

    /* conversion */

    uk.gov.legislation.api.responses.Level convert() {
        uk.gov.legislation.api.responses.Level other = new uk.gov.legislation.api.responses.Level();
        other.name = this.name;
        other.id = this.id;
        other.href = Links.shorten(uri);
        other.number = this.number;
        other.title = this.title;
        other.label = Labels.make(this);
        return other;
    }

}
