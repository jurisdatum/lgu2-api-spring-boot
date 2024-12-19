package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.util.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class Metadata implements uk.gov.legislation.endpoints.document.MetaData {

    private String id;

    public String id() { return id; }

    private String longType;

    public String longType() { return longType; }

    public String shortType() { return Types.longToShort(longType); }

    private int year;

    public int year() { return year; }

    private String regnalYear;

    public String regnalYear() { return regnalYear; }

    private int number;

    public int number() { return number; }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "altNumber")
    private List<AltNum> altNums;

    public List<AltNum> altNumbers() { return altNums; }

    public static class AltNum implements uk.gov.legislation.util.AltNumber, DocumentList.Document.AltNumber {

        @JacksonXmlProperty(isAttribute = true)
        private String category;

        @Override
        public String category() {
            return category;
        }

        @JacksonXmlProperty(isAttribute = true)
        private String value;

        @Override
        public String value() {
            return value;
        }

    }

    private LocalDate date;

    public LocalDate date() { return date; }

    public String cite() {
        return Cites.make(longType, year, number, altNumbers());
    }

    private LocalDate valid;
    @JsonSetter("valid")
    public void setValid(String value) { valid = LocalDate.parse(value); }

    public String version() {
        if (valid != null)
            return valid.toString();
        return FirstVersion.get(longType);
    }

    private String status;

    public String status() { return status; }

    private String title;

    public String title() { return title; }

    private String lang;

    public String lang() { return lang; }

    private String publisher;

    public String publisher() { return publisher; }

    private LocalDate modified;

    public LocalDate modified() { return modified; }

    private List<String> versions;

    public List<String> versions() {
        LinkedHashSet<String> set = new LinkedHashSet<>(versions);
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
    public void setVersions(List<String> value) { versions = value; }

    private boolean schedules;

    public boolean schedules() { return schedules; }
    @JsonSetter("schedules")
    public void setSchedules(String value) { schedules = value != null && !value.isBlank(); }

    /* formats */

    public static class Format {

        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlProperty(isAttribute = true)
        private String uri;

    }

    private List<Format> formats;

    @JacksonXmlElementWrapper(localName = "formats")
    @JacksonXmlProperty(localName = "format")
    @JsonSetter
    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public List<String> formats() { return formats.stream().map(f -> f.name).toList(); }

    public Optional<String> pdfFormatUri() {
       return formats.stream().filter(f -> "pdf".equals(f.name)).map(f -> f.uri).findAny();
    }

    /* fragment info */

    private String fragment;
    public String fragment() { return fragment; }
    @JsonSetter("fragment")
    public void setFragment(String value) { fragment = String.valueOf(Links.extractFragmentIdentifierFromLink(value)); }

    private String prev;
    public String prev() { return prev; }
    @JsonSetter("prev")
    public void setPrev(String value) { prev = String.valueOf(Links.extractFragmentIdentifierFromLink(value)); }

    private String next;
    public String next() { return next; }
    @JsonSetter("next")
    public void setNext(String value) { next = String.valueOf(Links.extractFragmentIdentifierFromLink(value)); }

}
