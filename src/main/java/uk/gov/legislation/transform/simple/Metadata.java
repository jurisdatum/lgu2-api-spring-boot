package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;
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
        if (FINAL.equals(status))
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

    /**
     * {@code dct:valid}: valid-from date of the returned revised representation.
     *
     * <p>This is not necessarily the requested point-in-time from the URL, a version label, or a
     * fragment milestone. For fragment responses it can be later than the selected fragment
     * milestone when the fragment is served from a later revised representation.</p>
     */
    private LocalDate valid;
    @JsonSetter("valid")
    public void setValid(String value) { valid = LocalDate.parse(value); }

    public static final String FINAL = "final";
    public static final String REVISED = "revised";

    private static final String PROSPECTIVE = "prospective";
    private static final String CURRENT = "current";
    private static final String REPEALED = " repealed";

    private String _computedVersion;

    /**
     * Label selected by the request.
     *
     * <p>{@code dct:valid} is the valid-from date of the returned revised representation and need
     * not itself be a milestone. For ordinary revised responses, choose the latest emitted
     * milestone that is not after that valid-from date. For prospective revised responses, use the
     * label-only {@code prospective} value that mirrors legislation.gov.uk's timeline label.</p>
     */
    public String version() {
        if (_computedVersion != null)
            return _computedVersion;
        _computedVersion = computeVersion();
        return _computedVersion;
    }

    private String computeVersion() {
        if (isProspectiveRevised())
            return PROSPECTIVE;
        if (valid == null)
            return FirstVersion.getFirstVersion(longType);
        String selected = latestEligibleMilestoneNotAfter(valid);
        // A null result means the normalised version set contains no first-version keyword and no
        // dated milestone on or before dct:valid. The response is still a revised snapshot valid
        // from dct:valid, so dct:valid is the only value we have that identifies the returned
        // revision.
        return selected == null ? valid.toString() : selected;
    }

    /**
     * Selects the latest milestone in {@link #versions()} that can describe a revised snapshot
     * valid from {@code dctValid}.
     *
     * <p>{@link #versions()} returns labels sorted by {@link Versions#COMPARATOR}: first-version
     * keywords first, then ISO dates chronologically, then {@code prospective}. This method walks
     * that set and keeps replacing {@code selected} when it sees an eligible label: the
     * type-specific first-version keyword, or an ISO date that is not after {@code dctValid}.
     * Non-date labels such as {@code prospective} are skipped. Because the scan is ordered, the
     * value left in {@code selected} at the end is the latest eligible milestone.</p>
     */
    private String latestEligibleMilestoneNotAfter(LocalDate dctValid) {
        String firstVersion = FirstVersion.getFirstVersion(longType);
        String selected = null;
        for (String label : versions()) {
            if (label.equals(firstVersion)) {
                selected = label;
                continue;
            }
            try {
                LocalDate milestone = LocalDate.parse(label);
                if (!milestone.isAfter(dctValid))
                    selected = label;
            } catch (DateTimeParseException ignored) {}
        }
        return selected;
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

    /**
     * A single {@code <hasVersion>} entry, carrying the source link's {@code hreflang} attribute
     * so language-aware filtering can tell Welsh links apart from English ones in bilingual XML.
     */
    public static class HasVersionEntry {

        /**
         * BCP 47 language tag of the source {@code atom:link} ({@code "en"} or {@code "cy"}),
         * or {@code null} when the link carries no {@code @hreflang}. Untagged entries apply to
         * both languages.
         */
        @JacksonXmlProperty(isAttribute = true)
        public String hreflang;

        /**
         * The milestone label from the source link's {@code @title}. Either an ISO date
         * ({@code "2024-11-22"}), a first-version keyword ({@code "enacted"}, {@code "made"},
         * {@code "created"}, {@code "adopted"}), the {@code "current"} alias for the latest
         * snapshot, a literal {@code "prospective"}, or a dated-and-repealed label
         * ({@code "2020-12-31 repealed"}) which is normalised back to its base date by
         * {@link #versions()}.
         */
        @JacksonXmlText
        public String title;

        public static HasVersionEntry of(String hreflang, String title) {
            HasVersionEntry entry = new HasVersionEntry();
            entry.hreflang = hreflang;
            entry.title = title;
            return entry;
        }
    }

    private List<HasVersionEntry> _versions;

    @JacksonXmlElementWrapper(localName = "hasVersions")
    @JacksonXmlProperty(localName = "hasVersion")
    @JsonSetter
    public void setVersions(List<HasVersionEntry> value) { _versions = value; }

    private TreeSet<String> _computedVersions;

    /**
     * Normalised, ordered version labels available in this response's scope.
     *
     * <p>The scope is the thing requested: whole-document responses expose document milestones;
     * fragment responses expose fragment milestones. In bilingual XML, the scope is further
     * narrowed to links for the response language, plus untagged links. The returned
     * {@code dct:valid} date is not automatically in this scope; for a fragment it may identify a
     * containing-document snapshot rather than a fragment milestone.</p>
     *
     * <p>The method first builds the scoped label set from {@code hasVersion} links, then removes
     * unstable aliases, and finally repairs the few cases where legislation.gov.uk omits a label
     * that the API still needs to expose.</p>
     *
     * <ol>
     *   <li>Keep {@code hasVersion} entries whose {@code hreflang} matches the response language,
     *       plus any untagged entries.</li>
     *   <li>Strip the trailing {@code " repealed"} suffix.</li>
     *   <li>Remove the {@code current} alias; remember whether it was present and whether it was
     *       the only retained link.</li>
     *   <li>For {@code final} status, ensure the type-specific first-version keyword is present.</li>
     *   <li>For {@code final} XML where {@code current} was the only retained entry, synthesise a
     *       label-only {@code prospective} entry.</li>
     *   <li>For prospective revised content, synthesise a label-only {@code prospective}
     *       entry.</li>
     *   <li>Otherwise, when {@code current} was present alongside {@code dct:valid}, add that date
     *       if it identifies the returned representation in this scope: always for whole-document
     *       responses, and for fragment responses only when no other dated fragment labels remain.</li>
     * </ol>
     */
    public SortedSet<String> versions() {
        if (_computedVersions != null)
            return _computedVersions;
        if (_versions == null)
            _versions = List.of();
        TreeSet<String> result = new TreeSet<>(Versions.COMPARATOR);
        for (HasVersionEntry entry : _versions) {
            if (!matchesResponseLanguage(entry.hreflang))
                continue;
            result.add(stripRepealed(entry.title));
        }
        boolean hadCurrent = result.remove(CURRENT);
        boolean onlyCurrent = hadCurrent && result.isEmpty();
        boolean isFinal = FINAL.equals(status);
        if (isFinal)
            result.add(FirstVersion.getFirstVersion(longType));
        if (isFinal && onlyCurrent)
            result.add(PROSPECTIVE);
        if (isProspectiveRevised())
            result.add(PROSPECTIVE);
        else if (!isFinal && valid != null && shouldRecoverValidDate(hadCurrent, result))
            result.add(valid.toString());
        _computedVersions = result;
        return _computedVersions;
    }

    /**
     * Whether {@code dct:valid} should be added to {@link #versions()} as a recovered label.
     *
     * <p>Stripping the {@code current} alias can leave the result set with no label identifying
     * the returned representation. {@code dct:valid} is the valid-from date of that
     * representation, so using it as a label recovers the identity that was lost — but only when
     * {@code current} was actually present in the scoped input; otherwise there is nothing to
     * recover.</p>
     *
     * <p>The whole-document vs fragment asymmetry: a whole-document response's {@code dct:valid}
     * identifies the returned document snapshot, so when {@code current} was stripped we recover
     * it as the missing document label. A fragment response's {@code dct:valid} may be a
     * containing-document snapshot date rather than a fragment milestone, so we recover it only
     * when no other dated fragment label is already present — i.e. as a last-resort label when
     * fragment-specific milestones are unavailable.</p>
     */
    private boolean shouldRecoverValidDate(boolean hadCurrent, SortedSet<String> labels) {
        if (!hadCurrent)
            return false;
        return isWholeDocument() || !hasDatedLabel(labels);
    }

    private boolean isProspectiveRevised() {
        return prospective && REVISED.equals(status);
    }

    private static boolean hasDatedLabel(SortedSet<String> labels) {
        for (String s : labels)
            if (Versions.isDateLabel(s))
                return true;
        return false;
    }

    /**
     * True when the payload is a whole-document response rather than a fragment response.
     * {@code simplify/metadata.xsl} only emits the {@code <fragment>} element (from which
     * {@link #fragment} is populated) when the {@code is-fragment} parameter is true, so a null
     * {@code fragment} uniquely indicates a whole-document request.
     */
    private boolean isWholeDocument() {
        return fragment == null;
    }

    private boolean matchesResponseLanguage(String hreflang) {
        return hreflang == null || lang == null || lang.equals(hreflang);
    }

    private static String stripRepealed(String title) {
        return title.endsWith(REPEALED) ? title.substring(0, title.length() - REPEALED.length()) : title;
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

    /**
     * Descendants of the current payload. Only populated for fragment requests.
     *
     * <p>By convention of {@code simplify/metadata.xsl}, the first entry describes the target
     * fragment itself; {@link uk.gov.legislation.converters.FragmentMetadataConverter} relies on
     * that to populate {@code fragmentInfo}.
     */
    @JacksonXmlProperty(localName = "descendants")
    private List<Descendant> descendants = Collections.emptyList();

    public List<uk.gov.legislation.api.responses.Level> descendants() {
        return descendants.stream().map(Level::convert).toList();
    }

    /**
     * True when the target element carries {@code Status="Prospective"}.
     *
     * <p>Set by {@code simplify/metadata.xsl} from the target's {@code @Status} attribute —
     * the root {@code <Legislation>} for whole-document requests, or the fragment element for
     * fragment requests. Drives the "add {@code prospective} to versions" rule for prospective
     * revised content.
     */
    @JacksonXmlProperty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean prospective;

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
