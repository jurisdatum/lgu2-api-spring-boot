package uk.gov.legislation.util;

import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.transform.simple.effects.Effect;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Effects {

    private Effects() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /* sections */

    static final Predicate<RichTextNode> isSection = node -> RichTextNode.SECTION_TYPE.equals(node.type);

    static final Predicate<Effect> isPlainText = effect -> effect.affectedProvisions.stream()
        .noneMatch(isSection);

    /**
     *
     * @param effects
     * @param ids the fragment ids to be retained
     * @param includeWhole retain those targeting no particular section
     * @return
     */
    public static List<Effect> removeThoseWithNoRelevantSection(List<Effect> effects, Set<String> ids, boolean includeWhole) {
        Predicate<Effect> someSectionsAreRelevant = effect -> effect.affectedProvisions.stream()
            .filter(isSection).anyMatch(n -> ids.contains(n.ref));
        if (includeWhole)
            return effects.stream().filter(isPlainText.or(someSectionsAreRelevant)).toList();
        else
            return effects.stream().filter(someSectionsAreRelevant).toList();
    }

}
