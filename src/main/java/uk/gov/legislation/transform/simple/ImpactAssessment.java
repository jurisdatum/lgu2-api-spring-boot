package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;
import java.time.LocalDate;

public class ImpactAssessment {

    @JacksonXmlProperty(isAttribute = true, localName = "URI")
    public URI uri;

    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    public LocalDate date;

    @JacksonXmlProperty(isAttribute = true, localName = "Title")
    public String title;

    @JacksonXmlProperty(isAttribute = true, localName = "TitleWelsh")
    public String welshTitle;

    @JacksonXmlProperty(isAttribute = true, localName = "Stage")
    public String stage;

    @JacksonXmlProperty(isAttribute = true, localName = "Department")
    public String department;

    @JacksonXmlProperty(isAttribute = true, localName = "Year")
    public String year;

    @JacksonXmlProperty(isAttribute = true, localName = "Number")
    public String number;

    @JacksonXmlProperty(isAttribute = true, localName = "Language")
    public String language;

    @JacksonXmlProperty(isAttribute = true, localName = "Size")
    public Integer size;

}
