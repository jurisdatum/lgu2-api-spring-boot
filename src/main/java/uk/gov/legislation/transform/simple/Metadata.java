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

    private static Optional<LocalDate> tryParseDate(String s) {
        try {
            return Optional.of(LocalDate.parse(s));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Determines the appropriate version identifier for this legislation document or fragment.
     *
     * <p>The version logic follows these rules:
     * <ol>
     * <li>If no dct:valid date exists, this is an original (unrevised) version.
     *     Returns the type-specific original version name (enacted, made, created, or adopted).</li>
     * <li>If dct:valid date exists, this is a revised document that should have dated versions.
     *     If no dated versions are found (defensive fallback), returns the dct:valid date.</li>
     * <li>For document fragments: if dct:valid date is after the last actual revision date
     *     (due to other document parts being amended more recently), returns the fragment's
     *     last actual revision date instead of the overall document valid date.</li>
     * <li>Otherwise, returns the dct:valid date as the current version.</li>
     * </ol>
     *
     * @return the version identifier string (either a type-specific name or an ISO date)
     */
    public String version() {
        if (valid == null)
            return FirstVersion.getFirstVersion(longType);
        Optional<LocalDate> last = versions().reversed().stream()
            .map(Metadata::tryParseDate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
        if (last.isEmpty())
            return valid.toString();
        if (valid.isAfter(last.get()))
            return last.get().toString();
        return valid.toString();
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

    @JacksonXmlElementWrapper(localName = "hasVersions")
    @JacksonXmlProperty(localName = "hasVersion")
    @JsonSetter
    public void setVersions(List<String> value) { _versions = value; }

    private TreeSet<String> _versions2;

    private static final String REPEALED = " repealed";

    public SortedSet<String> versions() {
        if (_versions2 != null)
            return _versions2;
        _versions2 = new TreeSet<>(Versions.COMPARATOR);
        _versions2.addAll(_versions);
        if (_versions2.remove("current")) {
            if (this.valid != null)
                _versions2.add(this.valid.toString());  // TODO check
        }
        if ("final".equals(status)) {
            String first = FirstVersion.getFirstVersion(longType);
            _versions2.add(first);
        }
        if (!_versions2.isEmpty()) {
            String last = _versions2.last();
            if (last.endsWith(REPEALED)) {
                _versions2.pollLast();
                String base = last.substring(0, last.length() - REPEALED.length());
                _versions2.add(base);
            }
        }
        return _versions2;
    }

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
