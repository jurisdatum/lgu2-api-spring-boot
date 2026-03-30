package uk.gov.legislation.data.marklogic.models;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDate;

public class LocalDateValue {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public LocalDate value;

}
