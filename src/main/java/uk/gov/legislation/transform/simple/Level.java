package uk.gov.legislation.transform.simple;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.util.Labels;
import uk.gov.legislation.util.Links;

import java.time.LocalDate;

public class Level {

    @JacksonXmlProperty(isAttribute = true)
    public String name;

    @JacksonXmlProperty(isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "DocumentURI", isAttribute = true)
    public String uri;

    @JacksonXmlProperty(localName = "Status", isAttribute = true)
    // Prospective, Dead, Discarded, Repealed
    public String status;

    @JacksonXmlProperty(localName = "RestrictStartDate", isAttribute = true)
    public LocalDate start;

    @JacksonXmlProperty(localName = "RestrictEndDate", isAttribute = true)
    public LocalDate end;

    @JacksonXmlProperty
    public String number;

    @JacksonXmlProperty
    public String title;

    /* conversion */

    /* mabye this should be moved to the .converters package? */

    uk.gov.legislation.api.responses.Level convert() {
        uk.gov.legislation.api.responses.Level other = new uk.gov.legislation.api.responses.Level();
        other.element = this.name;
        other.id = this.id;
        other.href = Links.extractFragmentIdentifierFromLink(uri);
        other.number = this.number;
        other.title = this.title;
        other.label = Labels.make(this);
        other.prospective = "Prospective".equals(this.status);
        other.start = this.start;
        other.end = this.end;
        return other;
    }

}
