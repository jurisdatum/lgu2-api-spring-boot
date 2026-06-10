package uk.gov.legislation.transform.simple.effects;

import java.time.LocalDate;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class InForce {

    @JacksonXmlProperty(localName = "Date", isAttribute = true)
    public LocalDate date;

    @JacksonXmlProperty(localName = "Applied", isAttribute = true)
    public boolean applied;

    @JacksonXmlProperty(localName = "Prospective", isAttribute = true)
    public Boolean prospective;

    @JacksonXmlProperty(localName = "Qualification", isAttribute = true)
    public String qualification;
}
