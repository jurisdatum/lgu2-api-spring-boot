package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;
import java.time.LocalDate;

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
