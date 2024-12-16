package uk.gov.legislation.transform.simple.effects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import net.sf.saxon.s9api.XdmNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * the purpose of this class is simply to map the 'simplified' CLML using Jackson
 */
@JacksonXmlRootElement(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
public class UnappliedEffect {

    @JacksonXmlProperty(localName = "RequiresApplied", isAttribute = true)
    public boolean requiresApplied;

//    @JacksonXmlProperty(localName = "RequiresWelshApplied", isAttribute = true)
//    public boolean requiresWelshApplied;

    @JacksonXmlProperty(localName = "Type", isAttribute = true)
    public String type;

    @JacksonXmlProperty(localName = "AffectingClass", isAttribute = true)
    public String affectingClass;

    @JacksonXmlProperty(localName = "AffectingYear", isAttribute = true)
    public int affectingYear;

    @JacksonXmlProperty(localName = "AffectingNumber", isAttribute = true)
    public int affectingNumber;

    @JacksonXmlProperty(localName = "AffectingURI", isAttribute = true)
    public String affectingURI;

    @JacksonXmlProperty(localName = "Notes", isAttribute = true)
    public String notes;

    // @AffectedProvisions attribute removed in simplify.xsl
    @JacksonXmlElementWrapper(localName = "AffectedProvisions")
    @JacksonXmlProperty(localName = "Section")
    public List<Section> affectedProvisions = Collections.emptyList();

    @JacksonXmlElementWrapper(localName = "InForceDates")
    @JacksonXmlProperty(localName = "InForce")
    public List<InForce> inForceDates;

    public UnappliedEffect copy() {
        UnappliedEffect e2 = new UnappliedEffect();
        e2.requiresApplied = this.requiresApplied;
        e2.type = this.type;
        e2.affectingClass = this.affectingClass;
        e2.affectingYear = this.affectingYear;
        e2.affectingNumber = this.affectingNumber;
        e2.affectingURI = this.affectingURI;
        e2.notes = this.notes;
        e2.affectedProvisions = this.affectedProvisions.stream().map(Section::copy).toList();
        e2.inForceDates = this.inForceDates.stream().map(InForce::copy).toList();
        return e2;
    }

    public static class Section {

        // @err:Ref attribute removed in simplify.xsl
        @JacksonXmlProperty(localName = "Ref", isAttribute = true)
        public String ref;

        @JacksonXmlText
        public String text;

        @JacksonXmlProperty(localName = "Missing", isAttribute = true)
        public boolean missing;

        public Section copy() {
            Section s2 = new Section();
            s2.ref = this.ref;
            s2.text = this.text;
            s2.missing = this.missing;
            return s2;
        }

    }

    public static class InForce {

        @JacksonXmlProperty(localName = "Applied", isAttribute = true)
        public boolean applied;

        @JacksonXmlProperty(localName = "Date", isAttribute = true)
        public LocalDate date;

        @JacksonXmlProperty(localName = "Qualification", isAttribute = true)
        public String qualification;

        public InForce copy() {
            InForce if2 = new InForce();
            if2.applied = this.applied;
            if2.date = this.date;
            if2.qualification = this.qualification;
            return if2;
        }

    }

    /* with Saxon */

    public static UnappliedEffect make(XdmNode e) {
        UnappliedEffect effect = new UnappliedEffect();
        effect.requiresApplied = Boolean.parseBoolean(e.attribute("RequiresApplied"));
//        effect.requiresWelshApplied = Boolean.parseBoolean(e.attribute("RequiresWelshApplied"));
        effect.type = e.attribute("Type");
        effect.affectingClass = e.attribute("AffectingClass");
        effect.affectingYear = Integer.parseInt(e.attribute("AffectingYear"));
        effect.affectingNumber = Integer.parseInt(e.attribute("AffectingNumber"));
        effect.affectingURI = e.attribute("AffectingURI");
        effect.notes = e.attribute("Notes");
        effect.affectedProvisions = makeAffected(e);
        effect.inForceDates = makeInForce(e);
        return effect;
    }

    private static List<Section> makeAffected(XdmNode effect) {
        Iterator<XdmNode> it = effect.children("AffectedProvisions").iterator();
        if (!it.hasNext())
            return Collections.emptyList();
        List<Section> affected = new ArrayList<>();
        Iterable<XdmNode> children = it.next().children("Section");
        for (XdmNode child: children) {
            Section section = new Section();
            section.ref = child.attribute("Ref");
            section.missing = Boolean.parseBoolean(child.attribute("Missing"));
            affected.add(section);
        }
        return affected;
    }

    private static List<InForce> makeInForce(XdmNode effect) {
        Iterator<XdmNode> it = effect.children("InForceDates").iterator();
        if (!it.hasNext())
            return Collections.emptyList();
        List<InForce> inForceDates = new ArrayList<>();
        Iterable<XdmNode> children = it.next().children("InForce");
        for (XdmNode child: children) {
            InForce inForce = new InForce();
            String date = child.attribute("Date");
            if (date != null)
                inForce.date = LocalDate.parse(child.attribute("Date"));
            inForce.applied = Boolean.parseBoolean(child.attribute("Applied"));
            inForce.qualification = child.attribute("Qualification");
            inForceDates.add(inForce);
        }
        return inForceDates;
    }

}
