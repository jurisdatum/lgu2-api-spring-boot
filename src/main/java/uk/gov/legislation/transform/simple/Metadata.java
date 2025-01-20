package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.FirstVersion;
import uk.gov.legislation.util.Links;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public class Metadata {

    public String id;

    public String longType;

    public int year;

    public String regnalYear;

    public int number;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "altNumber")
    @JsonIgnore
    public List<AltNum> altNums;

    public static class AltNum implements uk.gov.legislation.util.AltNumber {

        @JacksonXmlProperty(isAttribute = true)
        public String category;

        @Override
        public String category() {
            return category;
        }

        @JacksonXmlProperty(isAttribute = true)
        public String value;

        @Override
        public String value() {
            return value;
        }

    }

    public LocalDate date;

    private LocalDate valid;
    @JsonSetter("valid")
    public void setValid(String value) { valid = LocalDate.parse(value); }

    public String version() {
        if (valid != null)
            return valid.toString();
        return FirstVersion.get(longType);
    }

    public String status;

    public String title;

    public String extent;

    public String lang;

    public String publisher;

    public LocalDate modified;

    private List<String> _versions;

    public List<String> versions() {
        LinkedHashSet<String> set = new LinkedHashSet<>(_versions);
        if (set.contains("current")) { // FixMe this might put current version out of order
            set.remove("current");
            set.add(this.version());
        }
        set.remove("prospective"); // FixMe
        return set.stream().toList();
    }

    @JacksonXmlElementWrapper(localName = "hasVersions")
    @JacksonXmlProperty(localName = "hasVersion")
    @JsonSetter
    public void setVersions(List<String> value) { _versions = value; }

    public boolean schedules;

    @JsonSetter("schedules")
    public void setSchedules(String value) { schedules = value != null && !value.isBlank(); }

    /* formats */

    public static class Format {

        @JacksonXmlProperty(isAttribute = true)
        public String name;

        @JacksonXmlProperty(isAttribute = true)
        public String uri;

    }

    private List<Format> _formats;

    @JacksonXmlElementWrapper(localName = "formats")
    @JacksonXmlProperty(localName = "format")
    @JsonSetter
    public void setFormats(List<Format> formats) { _formats = formats; }

    public List<String> formats() { return _formats.stream().map(f -> f.name).toList(); }

    public Optional<String> pdfFormatUri() {
       return _formats.stream().filter(f -> "pdf".equals(f.name)).map(f -> f.uri).findAny();
    }

    /* fragment info */

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

    /* ancestors and descendants */

    public static class Ancestor extends Level {}

    @JacksonXmlProperty(localName = "ancestors")
    private List<Ancestor> ancestors = Collections.emptyList();

    public List<uk.gov.legislation.api.responses.Level> ancestors() {
        return ancestors.stream().map(Level::convert).toList();
    }

    public static class Descendant extends Level {}

    @JacksonXmlProperty(localName = "descendants")
    private List<Descendant> descendants = Collections.emptyList();

    public List<uk.gov.legislation.api.responses.Level> descendants() {
        return descendants.stream().map(Level::convert).toList();
    }

    /* unapplied effects */

    @JacksonXmlElementWrapper(localName = "UnappliedEffects", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "UnappliedEffect", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Effect> rawEffects = Collections.emptyList();

}
