package uk.gov.legislation.transform.simple;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;
import java.time.LocalDate;

/**
 * Jackson mapping for the "AlterType" complex type in schemaLegislationMetadata.xsd.
 * Represents general-purpose associated documents like alternative formats, correction slips,
 * explanatory documents, etc.
 */
public class Alternative {

    @JacksonXmlProperty(isAttribute = true, localName = "URI")
    public URI uri;

    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    public LocalDate date;

    @JacksonXmlProperty(isAttribute = true, localName = "Title")
    public String title;

    @JacksonXmlProperty(isAttribute = true, localName = "TitleWelsh")
    public String welshTitle;

    @JacksonXmlProperty(isAttribute = true, localName = "Language")
    public String language;

    @JacksonXmlProperty(isAttribute = true, localName = "Size")
    public Integer size;

    @JacksonXmlProperty(isAttribute = true, localName = "Revised")
    public LocalDate revised;

    @JacksonXmlProperty(isAttribute = true, localName = "Print")
    public Boolean print;

}
