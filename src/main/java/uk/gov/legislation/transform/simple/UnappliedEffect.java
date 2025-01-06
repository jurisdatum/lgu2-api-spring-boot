package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/*
 * the purpose of this class is simply to map the 'simplified' CLML using Jackson
 */
@JacksonXmlRootElement(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
public class UnappliedEffect {

    @JacksonXmlProperty(localName = "RequiresApplied", isAttribute = true)
    public boolean requiresApplied;

    @JacksonXmlProperty(localName = "Type", isAttribute = true)
    public String type;

    /* affecting */

    @JacksonXmlProperty(localName = "AffectingClass", isAttribute = true)
    public String affectingClass;

    @JacksonXmlProperty(localName = "AffectingYear", isAttribute = true)
    public int affectingYear;

    @JacksonXmlProperty(localName = "AffectingNumber", isAttribute = true)
    public int affectingNumber;

    @JacksonXmlProperty(localName = "AffectingURI", isAttribute = true)
    public String affectingURI;

    @JacksonXmlProperty(localName = "AffectingProvisionsText", isAttribute = true)
    public String affectingProvisionsText;

    @JacksonXmlProperty(localName = "AffectingProvisions")
    public List<RichTextNode> affectingProvisions = Collections.emptyList();

    @JacksonXmlProperty(localName = "AffectingExtent", isAttribute = true)
    public String affectingExtent;

    @JacksonXmlProperty(localName = "AffectingEffectsExtent", isAttribute = true)
    public String affectingEffectsExtent;

    /* affected */

    @JacksonXmlProperty(localName = "AffectedProvisionsText", isAttribute = true)
    public String affectedProvisionsText;

    @JacksonXmlProperty(localName = "AffectedProvisions")
    public List<RichTextNode> affectedProvisions = Collections.emptyList();

    @JacksonXmlProperty(localName = "AffectedExtent", isAttribute = true)
    public String affectedExtent;

    @JacksonXmlProperty(localName = "AffectedEffectsExtent", isAttribute = true)
    public String affectedEffectsExtent;

    /* other */

    @JacksonXmlProperty(localName = "Notes", isAttribute = true)
    public String notes;

    @JacksonXmlElementWrapper(localName = "InForceDates")
    @JacksonXmlProperty(localName = "InForce")
    public List<InForce> inForceDates = Collections.emptyList();

    /* Provide details of the provision giving authority for the In-Force date(s).
    /* If there is no commencement provision then there will be a value added 'N'.
    /* Where the commencement date is defined elsewhere, for example, in an Interpretation provision,
    /* then this provision also will be included as part of the Commencement Authority */
    @JacksonXmlProperty(localName = "CommencementAuthority")
    public List<RichTextNode> commencementAuthority = Collections.emptyList();

    /* rich text */

    public static class RichTextNode {

        public static final String TEXT_TYPE = "text";
        public static final String SECTION_TYPE = "section";

        @JacksonXmlProperty(isAttribute = true)
        public String type;

        @JacksonXmlProperty(isAttribute = true)
        public String text;

        @JacksonXmlProperty(isAttribute = true)
        public String ref;

        @JacksonXmlProperty(isAttribute = true)
        public String uri;

        @JacksonXmlProperty(isAttribute = true)
        public String error;

        @JacksonXmlProperty(isAttribute = true)
        public boolean missing;

    }

    public static class InForce {

        @JacksonXmlProperty(localName = "Date", isAttribute = true)
        public LocalDate date;

        @JacksonXmlProperty(localName = "Applied", isAttribute = true)
        public boolean applied;

        @JacksonXmlProperty(localName = "Prospective", isAttribute = true)
        public Boolean prospective;

        @JacksonXmlProperty(localName = "Qualification", isAttribute = true)
        public String qualification;

    }

}
