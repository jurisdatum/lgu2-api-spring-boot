package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.FirstVersion;
import uk.gov.legislation.util.Links;
import uk.gov.legislation.util.ShortTypes;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

public class Metadata implements uk.gov.legislation.api.document.Metadata {

    public String id;

    public String id() { return id; }

    public String longType;

    public String longType() { return longType; }

    public String shortType() { return ShortTypes.longToShort(longType); }

    public int year;

    public int year() { return year; }

//    public String regnalYear;

    public String regnalYear() { return null; }

    public int number;

    public int number() { return number; }

    public LocalDate date;

    public LocalDate date() { return date; }

    public String cite() {
        return Cites.make(longType, year, number);
    }

    private LocalDate valid;
    @JsonSetter("valid")
    public void setValid(String value) { valid = LocalDate.parse(value); }

    public String version() {
        if (valid != null)
            return valid.toString();
        return FirstVersion.get(longType);
    }

    public String status;

    public String status() { return status; }

    public String title;

    public String title() { return title; }

    public String lang;

    public String lang() { return lang; }

    public String publisher;

    public String publisher() { return publisher; }

    public LocalDate modified;

    public LocalDate modified() { return modified; }

    private List<String> _versions;

    public List<String> versions() {
        LinkedHashSet set = new LinkedHashSet<>(_versions);
        if (set.contains("current")) {
            set.remove("current");
            set.add(this.version());
        }
        set.remove("prospective"); // ?!
        return set.stream().toList();
    }

    @JacksonXmlElementWrapper(localName = "hasVersions")
    @JacksonXmlProperty(localName = "hasVersion")
    @JsonSetter
    public void setVersions(List<String> value) { _versions = value; }

    private String fragment;
    public String fragment() { return fragment; }
    @JsonSetter("fragment")
    public void setFragment(String value) { fragment = Links.extractFragmentIdentifierFromLink(value); }

    private String prev;
    public String prev() { return prev; }
    @JsonSetter("prev")
    public void setPrev(String value) { prev = Links.extractFragmentIdentifierFromLink(value); }

    private String next;
    public String next() { return next; }
    @JsonSetter("next")
    public void setNext(String value) { next = Links.extractFragmentIdentifierFromLink(value); }

    private boolean schedules;

    public boolean schedules() { return schedules; }
    @JsonSetter("schedules")
    public void setSchedules(String value) { schedules = value != null && !value.isBlank(); }

}