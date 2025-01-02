package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.endpoints.document.service.EffectsConverter;
import uk.gov.legislation.endpoints.document.service.ExtentConverter;
import uk.gov.legislation.endpoints.documents.DocumentList;
import uk.gov.legislation.util.*;

import java.time.LocalDate;
import java.util.*;

public class Metadata implements uk.gov.legislation.endpoints.document.Metadata {

    public String id;

    public String id() { return id; }

    public String longType;

    public String longType() { return longType; }

    public String shortType() { return Types.longToShort(longType); }

    public int year;

    public int year() { return year; }

    public String regnalYear;

    public String regnalYear() { return regnalYear; }

    public int number;

    public int number() { return number; }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "altNumber")
    @JsonIgnore
    public List<AltNum> altNums;

    public List<AltNum> altNumbers() { return altNums; }

    public static class AltNum implements uk.gov.legislation.util.AltNumber, DocumentList.Document.AltNumber {

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

    public String status;

    public String status() { return status; }

    public String title;

    public String title() { return title; }

    private String extent;

    public Set<Extent> extent() {
        return ExtentConverter.convert(extent);
    }

    public String lang;

    public String lang() { return lang; }

    public String publisher;

    public String publisher() { return publisher; }

    public LocalDate modified;

    public LocalDate modified() { return modified; }

    private List<String> _versions;

    public List<String> versions() {
        LinkedHashSet<String> set = new LinkedHashSet<>(_versions);
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

    private boolean schedules;

    public boolean schedules() { return schedules; }
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

    /* unapplied effects */

    private List<UnappliedEffect> rawEffects = Collections.emptyList();
    @JsonIgnore
    public List<UnappliedEffect> rawEffects() { return rawEffects; }

    private List<Effect> convertedEffects = Collections.emptyList();
    @Override
    public List<Effect> unappliedEffects() { return convertedEffects; }

    @JacksonXmlElementWrapper(localName = "UnappliedEffects", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "UnappliedEffect", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JsonSetter
    public void setUnappliedEffects(List<UnappliedEffect> source) {
        rawEffects = source;
        convertedEffects = EffectsConverter.convert(source);
    }

    private Set<String> internalIds = Collections.emptySet();

    @JsonIgnore
    public Set<String> getInternalIds() { return internalIds; }

    @JacksonXmlElementWrapper(localName = "internal-ids")
    @JacksonXmlProperty(localName = "internal-id")
    @JsonSetter
    public void setInternalIds(List<String> ids) { internalIds = new HashSet<>(ids); }

}
