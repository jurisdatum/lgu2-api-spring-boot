package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.FirstVersion;
import uk.gov.legislation.util.Links;

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

    private String fragment;
    @JsonGetter("fragmet")
    public String fragment() { return fragment; }
    @JsonSetter("fragment")
    public void setFragment(String value) { fragment = Links.extractFragmentIdentifierFromLink(value); }

    private String prev;
    @JsonGetter("prev")
    public String prev() { return prev; }
    @JsonSetter("prev")
    public void setPrev(String value) { prev = Links.extractFragmentIdentifierFromLink(value); }

    private String next;
    @JsonGetter("next")
    public String next() { return next; }
    @JsonSetter("next")
    public void setNext(String value) { next = Links.extractFragmentIdentifierFromLink(value); }

    private boolean schedules;

    @JsonGetter("schedules")
    public boolean schedules() { return schedules; }
    @JsonSetter("schedules")
    public void setSchedules(String value) { schedules = value != null && !value.isBlank(); }

}
