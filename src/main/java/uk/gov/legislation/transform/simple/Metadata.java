package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.FirstVersion;
import uk.gov.legislation.util.Links;
import uk.gov.legislation.util.Versions;

import java.net.URI;
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

    private static final String PROSPECTIVE = "prospective";

    /**
     * Determines the most appropriate version label for the current payload.
     *
     * <p>Rules applied in order:
     * <ol>
     * <li>No {@code dct:valid} → return the type-specific first-version label
     *     (e.g. enacted, made, created, adopted).</li>
     * <li>Scan {@link #versions()} from newest to oldest and pick the first ISO date available.
     *     If none exist, fall back to either {@link #PROSPECTIVE} (when the current fragment is
     *     marked {@code Status="Prospective"}) or the raw {@code dct:valid} string.</li>
     * <li>If the fragment-level {@code dct:valid} is newer than the latest dated label
     *     (typical for fragments lagging behind a whole-document revision), return that latest
     *     dated label instead of the fragment {@code dct:valid}.</li>
     * <li>Otherwise return the {@code dct:valid} value.</li>
     * </ol>
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
            return descendants.stream()  // first entry mirrors the current level; see descendants() doc
                .findFirst()
                .filter(d -> "Prospective".equals(d.status)).isPresent()
                ? PROSPECTIVE
                : valid.toString();
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

    /**
     * Produces a normalised, ordered set of version labels for this document or fragment.
     *
     * <p>Behavioural notes:
     * <ul>
     * <li>Start from the titles supplied via {@code atom:link[@rel='…hasVersion']}.</li>
     * <li>Strip the noisy {@code current} marker and, when present, substitute the appropriate
     *     fallback:
     *     <ul>
     *     <li>If {@link #status} is {@code final} and no other labels remain, assume a
     *         prospective snapshot exists and inject {@link #PROSPECTIVE}.</li>
     *     <li>Otherwise re-add the authoritative {@code dct:valid} date for this snapshot
     *         (unless {@link #version()} already resolved to {@link #PROSPECTIVE}).</li>
     *     </ul>
     * </li>
     * <li>Always ensure the type-specific first-version label is present for {@code final}
     *     documents.</li>
     * <li>Normalise any trailing {@code "… repealed"} markers back to their base date.</li>
     * <li>If {@link #version()} resolved to {@link #PROSPECTIVE}, add it once so the scalar and
     *     set stay in sync.</li>
     * </ul>
     *
     * @return version labels sorted by {@link Versions#COMPARATOR}
     */
    public SortedSet<String> versions() {
        if (_versions2 != null)
            return _versions2;
        _versions2 = new TreeSet<>(Versions.COMPARATOR);
        _versions2.addAll(_versions);
        if (_versions2.remove("current")) {
            if ("final".equals(status)) {
                if (_versions2.isEmpty())
                    _versions2.add(PROSPECTIVE);
            } else {
                if (!PROSPECTIVE.equals(version()) && this.valid != null) // should never be null if !"final".equals(status)
                    _versions2.add( this.valid.toString());
            }
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
        if (PROSPECTIVE.equals(version()))
            _versions2.add(PROSPECTIVE);
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

    public URI fragmentUri;
    private String fragment;

    @Deprecated(forRemoval = true)
    public String fragment() { return fragment; }

    @JsonSetter("fragment")
    public void setFragment(String value) {
        fragmentUri = URI.create(value);
        fragment = Links.extractFragmentIdentifierFromLink(value);
    }

    /* info for prev and next links */

    public URI prevUri;
    private String prev;

    @Deprecated(forRemoval = true)  // use prevUri
    public String prev() { return prev; }
    @JsonSetter("prev")

    public void setPrev(String value) {
        prevUri = URI.create(value);
        prev = Links.extractFragmentIdentifierFromLink(value);
    }

    /**
     * Raw label string from the Atom {@code link[@rel='prev']/@title} attribute.
     * The MarkLogic script generates this as a semicolon‑separated list of
     * components (e.g. "Provision; Section 2"). The converter uses only the
     * first component as the API label for now.
     */
    @JacksonXmlProperty
    public String prevTitle;

    public URI nextUri;
    private String next;  // to remove

    @Deprecated(forRemoval = true)  // use nextUri
    public String next() { return next; }
    @JsonSetter("next")

    public void setNext(String value) {
        nextUri = URI.create(value);
        next = Links.extractFragmentIdentifierFromLink(value);
    }

    /**
     * Raw label string from the Atom {@code link[@rel='next']/@title} attribute.
     * Like {@link #prevTitle}, this is semicolon‑separated; only the first
     * component is exposed by the API at present.
     */
    @JacksonXmlProperty
    public String nextTitle;

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

    /* simplify/metadata.xsl places the current fragment as the first descendant; version() relies on that. */

    public List<uk.gov.legislation.api.responses.Level> descendants() {
        return descendants.stream().map(Level::convert).toList();
    }

    /* unapplied effects */

    @JacksonXmlElementWrapper(localName = "UnappliedEffects", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "UnappliedEffect", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Effect> rawEffects = Collections.emptyList();

    /** Set to true when unapplied effects have been successfully fetched from MarkLogic for final documents. */
    @JsonIgnore
    public boolean finalEffectsEnriched = false;


    /* ConfersPower and BlanketAmendment */

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ConfersPower", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<TitledThing> confersPower;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "BlanketAmendment", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<TitledThing> blanketAmendments;


    /* notes|memorandum|executive-note|policy-note */

    @JacksonXmlProperty(localName = "Notes", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Notes notes;

    public static class Notes {

        @JacksonXmlElementWrapper(localName = "Alternatives", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @JacksonXmlProperty(localName = "Alternative", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public List<Alternative> alternatives;

        // ToDo there can be correction slips to notes?

    }

    @JacksonXmlElementWrapper(localName = "PolicyEqualityStatements", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "PolicyEqualityStatement", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> policyEqualityStatements;

    @JacksonXmlElementWrapper(localName = "Alternatives", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "Alternative", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> alternatives;

    @JacksonXmlElementWrapper(localName = "CorrectionSlips", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "CorrectionSlip", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> correctionSlips;

    @JacksonXmlElementWrapper(localName = "CodesOfPractice", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "CodeOfPractice", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> codesOfPractice;

    @JacksonXmlElementWrapper(localName = "CodesOfConduct", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "CodeOfConduct", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> codesOfConduct;

    @JacksonXmlElementWrapper(localName = "TablesOfOrigins", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "TableOfOrigins", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> tablesOfOrigins;

    @JacksonXmlElementWrapper(localName = "TablesOfDestinations", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "TableOfDestinations", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> tablesOfDestinations;

    @JacksonXmlElementWrapper(localName = "OrdersInCouncil", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "OrderInCouncil", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> ordersInCouncil;

    @JacksonXmlElementWrapper(localName = "ImpactAssessments", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "ImpactAssessment", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<ImpactAssessment> impactAssessments;

    @JacksonXmlElementWrapper(localName = "OtherDocuments", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "OtherDocument", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> otherDocuments;

    @JacksonXmlElementWrapper(localName = "ExplanatoryDocuments", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "ExplanatoryDocuments", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> explanatoryDocuments;

    @JacksonXmlElementWrapper(localName = "TranspositionNotes", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "TranspositionNote", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<Alternative> transpositionNotes;

    @JacksonXmlElementWrapper(localName = "UKRPCOpinions", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @JacksonXmlProperty(localName = "UKRPCOpinion", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<ImpactAssessment> ukrpcOpinions;

}
