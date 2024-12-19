package uk.gov.legislation.util;

import uk.gov.legislation.transform.simple.effects.UnappliedEffect;
import uk.gov.legislation.transform.simple.effects.UnappliedEffect.RichTextNode;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Effects {

    private Effects() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /* sections */

    static final Predicate<RichTextNode> isSection = node -> RichTextNode.SECTION_TYPE.equals(node.type);

    static final Predicate<UnappliedEffect> isPlainText = effect -> effect.affectedProvisions.stream()
        .noneMatch(isSection);

    public static List<UnappliedEffect> removeThoseWithNoRelevantSection(List<UnappliedEffect> effects, Set<String> ids) {
        if (ids.isEmpty())
            return effects;
        Predicate<UnappliedEffect> someSectionsAreRelevant = effect -> effect.affectedProvisions.stream()
            .filter(isSection).anyMatch(n -> ids.contains(n.ref));
        return effects.stream().filter(isPlainText.or(someSectionsAreRelevant)).toList();
    }

}
