package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.FirstVersion;
import uk.gov.legislation.util.Links;
import uk.gov.legislation.util.Versions;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Metadata {

    @JacksonXmlProperty(localName = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    public String dcIdentifier;

    public Optional<LocalDate> getPointInTime() {
        if ("final".equals(status))
            return Optional.empty();
        Links.Components comps = Links.parse(dcIdentifier);
        if (comps == null)
            return Optional.empty();
        if (comps.version().isEmpty())
            return Optional.empty();
        LocalDate date;
        try {
            date = LocalDate.parse(comps.version().get());
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
        return Optional.of(date);
    }


    public String id;

    public String longType;

    public int year;

    public String regnalYear;

    public Integer number;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "altNumber")
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

    public String isbn;

    public LocalDate date;

    private LocalDate valid;
    @JsonSetter("valid")
    public void setValid(String value) { valid = LocalDate.parse(value); }

    public String version() {
        if (valid != null)
            return valid.toString();
        return FirstVersion.getFirstVersion(longType);
    }

    public String status;

    public String title;

    public String extent;

    @JacksonXmlElementWrapper(localName = "subjects")
    @JacksonXmlProperty(localName = "subject")
    public List<String> subjects;

    public String lang;

    public String publisher;

    public LocalDate modified;

    private List<String> _versions;

    public SortedSet<String> versions() {
        SortedSet<String> set = new TreeSet<>(Versions.COMPARATOR);
        set.addAll(_versions);
        if (set.contains("current")) {
            set.remove("current");
            set.add(this.version());
        }
        if ("final".equals(status)) {
            String first = FirstVersion.getFirstVersion(longType);
            set.add(first);
        }
        return set;
    }

    @JacksonXmlElementWrapper(localName = "hasVersions")
    @JacksonXmlProperty(localName = "hasVersion")
    @JsonSetter
    public void setVersions(List<String> value) { _versions = value; }



    @JacksonXmlProperty
    public HasParts hasParts;

    public static class HasParts {

        @JacksonXmlProperty
        public String introduction;

        @JacksonXmlProperty
        public String signature;

        @JacksonXmlProperty
        public String schedules;

        @JacksonXmlProperty
        public String note;

        @JacksonXmlProperty
        public String earlierOrders;

    }

    @Deprecated
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
