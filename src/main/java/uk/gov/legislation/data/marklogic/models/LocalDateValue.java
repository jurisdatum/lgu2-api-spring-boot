package uk.gov.legislation.data.marklogic.models;

import java.time.LocalDate;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class LocalDateValue {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public LocalDate value;
}
