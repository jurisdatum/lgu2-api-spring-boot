package uk.gov.legislation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.transform.simple.effects.Effect;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

    static final Predicate<RichTextNode> isSection = node -> node instanceof RichTextNode.Section;

    static final Predicate<Effect> isPlainText = effect -> effect.affectedProvisions.stream()
        .noneMatch(isSection.or(node -> node instanceof RichTextNode.Range));

    /**
     *
     * @param effects
     * @param ids the fragment ids to be retained
     * @param includeWhole retain those targeting no particular section
     * @return
     */
    public static List<Effect> removeThoseWithNoRelevantSection(List<Effect> effects, Set<String> ids, boolean includeWhole) {
        Predicate<Effect> someSectionsAreRelevant = effect -> Effects.flatten(effect.affectedProvisions)
            .filter(isSection)
            .map(RichTextNode.Section.class::cast)
            .anyMatch(n -> ids.contains(n.ref));
        if (includeWhole)
            return effects.stream().filter(isPlainText.or(someSectionsAreRelevant)).toList();
        else
            return effects.stream().filter(someSectionsAreRelevant).toList();
    }

}
