package uk.gov.legislation.util;

import uk.gov.legislation.transform.simple.effects.UnappliedEffect;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Effects {

    private Effects() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /* sections */

    public static List<UnappliedEffect> removeIrrelevantSections(List<UnappliedEffect> effects, Set<String> ids) {
        return effects.stream()
            .map(e -> removeIrrelevantSections(e, ids))  // returns null if all affected provisions were removed
            .filter(Objects::nonNull)
            .toList();
    }
    private static UnappliedEffect removeIrrelevantSections(UnappliedEffect e, Set<String> ids) {
        if (e.affectedProvisions.isEmpty()) // empty means effect applies to entire document
            return e;
        List<UnappliedEffect.Section> sections =  e.affectedProvisions.stream().filter(s -> ids.contains(s.ref)).toList();
        if (sections.isEmpty())
            return null;
        UnappliedEffect copy = e.copy();
        copy.affectedProvisions = sections;
        return copy;
    }

    /* dates */

    public static List<UnappliedEffect> removeAppliedInForceDates(List<UnappliedEffect> effects) {
        return effects.stream()
            .map(Effects::removeAppliedInForceDates)
            .filter(Objects::nonNull)
            .toList();
    }
    private static UnappliedEffect removeAppliedInForceDates(UnappliedEffect e) {
        if (e.inForceDates.stream().noneMatch(s -> s.applied))
            return e;
        List<UnappliedEffect.InForce> inForce = e.inForceDates.stream().filter(s -> !s.applied).toList();
        if (inForce.isEmpty())
            return null;
        UnappliedEffect copy = e.copy();
        copy.inForceDates = inForce;
        return copy;
    }

}
