package uk.gov.legislation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.transform.simple.effects.Effect;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Effects {

    private static final Logger logger = LoggerFactory.getLogger(Effects.class);

    static Stream<RichTextNode> flatten(Collection<RichTextNode> nodes) {
        return nodes.stream().flatMap(Effects::flatten1);
    }

    static Stream<RichTextNode> flatten1(RichTextNode clml) {
        if (clml instanceof RichTextNode.Text text)
            return Stream.of(text);
        if (clml instanceof RichTextNode.Section section)
            return Stream.of(section);
        if (clml instanceof RichTextNode.Range range)
            return flatten(range.children);
        logger.warn("unrecognized type {}", clml.getClass());
        return null;
    }

    /* sections */

    static List<RichTextNode.Section> getAffectedSections(Effect effect) {
        return Effects.flatten(effect.affectedProvisions)
            .filter(node -> node instanceof RichTextNode.Section)
            .map(RichTextNode.Section.class::cast)
            .toList();
    }

    static List<RichTextNode.Range> getAffectedRanges(Effect effect) {
        return effect.affectedProvisions.stream()
            .filter(node -> node instanceof RichTextNode.Range)
            .map(RichTextNode.Range.class::cast)
            .toList();
    }

    static boolean rangeIncludesAny(RichTextNode.Range range, Set<String> ids) {
        return ids.stream().anyMatch(id -> rangeIncludes(range, id));
    }
    static boolean rangeIncludes(RichTextNode.Range range, String id) {
        if (range.start.equals(id))
            return true;
        if (range.end.equals(id))
            return true;
        return false;
    }

    static boolean relatesTo(Effect effect, Set<String> ids, boolean includeWhole) {
        List<RichTextNode.Section> sections = getAffectedSections(effect);
        if (sections.isEmpty())
            return includeWhole;
        if (sections.stream().anyMatch(n -> ids.contains(n.ref)))
            return true;
        List<RichTextNode.Range> ranges = getAffectedRanges(effect);
        return ranges.stream().anyMatch(range -> rangeIncludesAny(range, ids));
    }

    /**
     @param effects all the effects
     @param ids the fragment ids to be retained
     @param includeWhole retain those targeting no particular section
     @return a list of filtered effects
     */
    public static List<Effect> removeThoseWithNoRelevantSection(List<Effect> effects, Set<String> ids, boolean includeWhole) {
        return effects.stream().filter(effect -> relatesTo(effect, ids, includeWhole)).toList();
    }

}
