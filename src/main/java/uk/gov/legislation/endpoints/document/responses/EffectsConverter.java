package uk.gov.legislation.endpoints.document.responses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.transform.simple.effects.UnappliedEffect;
import uk.gov.legislation.util.Links;

import java.util.List;

public class EffectsConverter {

    private EffectsConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Logger logger = LoggerFactory.getLogger(EffectsConverter.class);

    public static List<Effect> convert(List<UnappliedEffect> effects) {
        return effects.stream().map(EffectsConverter::convert1).toList();
    }

    public static Effect convert1(UnappliedEffect clml) {
        Effect effect = new Effect();
        effect.type = clml.type;
        effect.requiresApplication = clml.requiresApplied;
        effect.affectedProvisions = convertAffectedProvisions(clml);
        effect.inForceDates = convertInForceDates(clml);
        effect.source = makeSource(clml);
        effect.notes = clml.notes;
        return effect;
    }

    private static List<Effect.Section> convertAffectedProvisions(UnappliedEffect clml) {
        return clml.affectedProvisions.stream().map(EffectsConverter::convertAffectedProvision).toList();
    }
    private static Effect.Section convertAffectedProvision(UnappliedEffect.Section clml) {
        Effect.Section section = new Effect.Section();
        section.id = clml.ref;
        section.missing = clml.missing;
        return section;
    }

    private static List<Effect.InForce> convertInForceDates(UnappliedEffect clml) {
        return clml.inForceDates.stream().map(EffectsConverter::convertInForceDate).toList();
    }
    private static Effect.InForce convertInForceDate(UnappliedEffect.InForce clml) {
        if (clml.applied)
            logger.warn("converting applied in-force date");
        Effect.InForce inForce = new Effect.InForce();
        inForce.date = clml.date;
        inForce.qualification = clml.qualification;
        return inForce;
    }

    private static Effect.Source makeSource(UnappliedEffect clml) {
        Effect.Source source = new Effect.Source();
        source.id = Links.shorten(clml.affectingURI);
        source.longType = clml.affectingClass;
        source.year = clml.affectingYear;
        source.number = clml.affectingNumber;
        return source;
    }

}
