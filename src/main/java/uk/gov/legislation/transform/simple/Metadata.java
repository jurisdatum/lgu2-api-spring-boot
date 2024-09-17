package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.FirstVersion;

import java.time.LocalDate;
import java.util.List;

public class Metadata {

    public String id;

    public String longType;

//    public String shortType;

    public int year;

//    public String regnalYear;

    public int number;

    public LocalDate date;

    @JsonProperty
    public String cite() {
        return Cites.make(longType, year, number);
    }

    @JsonIgnore
    public LocalDate valid;

    @JsonProperty
    public String version() {
        if (valid != null)
            return valid.toString();
        return FirstVersion.get(longType);
    }

    public String status;

    public String title;

    public String lang;

    public String publisher;

    public LocalDate modified;

    @JacksonXmlElementWrapper(localName = "versions")
    @JacksonXmlProperty(localName = "version")
    public List<String> versions;

}
