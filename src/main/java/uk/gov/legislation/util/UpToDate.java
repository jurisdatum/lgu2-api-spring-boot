package uk.gov.legislation.util;

import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.api.responses.Effect;
import uk.gov.legislation.api.responses.FragmentMetadata;

import java.time.LocalDate;

public class UpToDate {

    public static void setUpToDate(DocumentMetadata meta) {
        LocalDate today = LocalDate.now();
        setUpToDate(meta, today);
    }
    public static void setUpToDate(DocumentMetadata meta, LocalDate cutoff) {
        meta.unappliedEffects.forEach(e -> markEffect(e, cutoff));
        meta.upToDate = meta.unappliedEffects.stream().noneMatch(effect -> effect.outstanding);
    }

    public static void setUpToDate(FragmentMetadata meta) {
        LocalDate today = LocalDate.now();
        setUpToDate(meta, today);
    }
    public static void setUpToDate(FragmentMetadata meta, LocalDate cutoff) {
        meta.upToDate = UpToDate.mark(meta.unappliedEffects, cutoff);
    }

    // returns true if the fragment is up-to-date (e.g. none of the effects are outstanding
    private static boolean mark(FragmentMetadata.Effects effects, LocalDate cutoff) {
        effects.fragment.forEach(e -> markEffect(e, cutoff));
        effects.ancestor.forEach(e -> markEffect(e, cutoff));
        return effects.fragment.stream().noneMatch(effect -> effect.outstanding)
            && effects.ancestor.stream().noneMatch(effect -> effect.outstanding);
    }

    private static void markEffect(Effect effect, LocalDate cutoff) {
        effect.outstanding = false;
        if (effect.applied)
            return;
        if (!effect.required)
            return;
        effect.inForce.forEach(in -> markInForce(in, cutoff));
        effect.outstanding = effect.inForce.stream().anyMatch(inForce -> inForce.outstanding);
    }

    private static void markInForce(Effect.InForce inForce, LocalDate cutoff) {
        inForce.outstanding = false;
        if (inForce.applied)
            return;
        if (inForce.date == null)
            return;
        // check prospective?
        if (inForce.date.isAfter(cutoff))
            return;
        inForce.outstanding = true;
    }

}
