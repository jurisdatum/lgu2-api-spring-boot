package uk.gov.legislation.transform.simple.effects;

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.transform.simple.RichTextNode;

import java.util.Collections;
import java.util.List;

public class Effect {

    @JacksonXmlProperty(localName = "EffectId", isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName = "Applied", isAttribute = true)
    public boolean applied;

    @JacksonXmlProperty(localName = "RequiresApplied", isAttribute = true)
    public boolean requiresApplied;

    @JacksonXmlProperty(localName = "Type", isAttribute = true)
    public String type;

    /* affected */

    @JacksonXmlProperty(localName = "AffectedClass", isAttribute = true)
    public String affectedClass;

    @JacksonXmlProperty(localName = "AffectedYear", isAttribute = true)
    public int affectedYear;

    @JacksonXmlProperty(localName = "AffectedNumber", isAttribute = true)
    public int affectedNumber;

    @JacksonXmlProperty(localName = "AffectedURI", isAttribute = true)
    public String affectedURI;

    @JacksonXmlProperty(localName = "AffectedTitle", isAttribute = true)
    public String affectedTitle;

    @JacksonXmlProperty(localName = "AffectedProvisionsText", isAttribute = true)
    public String affectedProvisionsText;

    @JacksonXmlProperty(localName = "AffectedProvisions")
    public List<RichTextNode> affectedProvisions = Collections.emptyList();

    @JacksonXmlProperty(localName = "AffectedExtent", isAttribute = true)
    public String affectedExtent;

    @JacksonXmlProperty(localName = "AffectedEffectsExtent", isAttribute = true)
    public String affectedEffectsExtent;

    /* affecting */

    @JacksonXmlProperty(localName = "AffectingClass", isAttribute = true)
    public String affectingClass;

    @JacksonXmlProperty(localName = "AffectingYear", isAttribute = true)
    public int affectingYear;

    @JacksonXmlProperty(localName = "AffectingNumber", isAttribute = true)
    public int affectingNumber;

    @JacksonXmlProperty(localName = "AffectingURI", isAttribute = true)
    public String affectingURI;

    @JacksonXmlProperty(localName = "AffectingTitle", isAttribute = true)
    public String affectingTitle;

    @JacksonXmlProperty(localName = "AffectingProvisionsText", isAttribute = true)
    public String affectingProvisionsText;

    @JacksonXmlProperty(localName = "AffectingProvisions")
    public List<RichTextNode> affectingProvisions = Collections.emptyList();

    @JacksonXmlProperty(localName = "AffectingExtent", isAttribute = true)
    public String affectingExtent;

    @JacksonXmlProperty(localName = "AffectingEffectsExtent", isAttribute = true)
    public String affectingEffectsExtent;

    /* other */

    @JacksonXmlProperty(localName = "Notes", isAttribute = true)
    public String notes;

    @JacksonXmlElementWrapper(localName = "InForceDates")
    @JacksonXmlProperty(localName = "InForce")
    public List<InForce> inForceDates = Collections.emptyList();

    @JacksonXmlProperty(localName = "CommencementAuthority")
    public List<RichTextNode> commencementAuthority = Collections.emptyList();

}
