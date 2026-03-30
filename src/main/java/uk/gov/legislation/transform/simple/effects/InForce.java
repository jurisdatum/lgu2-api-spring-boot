package uk.gov.legislation.transform.simple.effects;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDate;

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
