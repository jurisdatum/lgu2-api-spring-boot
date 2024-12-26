package uk.gov.legislation.endpoints.document.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.endpoints.document.responses.RichText;
import uk.gov.legislation.transform.simple.effects.UnappliedEffect;
import uk.gov.legislation.util.Links;

import java.util.List;
import java.util.stream.Collectors;

public class EffectsConverter {

    private EffectsConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Logger logger = LoggerFactory.getLogger(EffectsConverter.class);

    public static List<Effect> convert(List<UnappliedEffect> effects) {
        return effects.stream().map(EffectsConverter::convertEffect).toList();
    }

    private static Effect convertEffect(UnappliedEffect clml) {
        Effect effect = new Effect();
        effect.type = clml.type;
        effect.required = clml.requiresApplied;
        effect.affected = convertProvisions(clml.affectedProvisionsText, clml.affectedProvisions);
        effect.inForceDates = convertInForceDates(clml);
        effect.source = makeSource(clml);
        effect.commencement = convertProvisions(clml.commencementAuthority);
        effect.notes = clml.notes;
        return effect;
    }

    private static Effect.Provisions convertProvisions(List<UnappliedEffect.RichTextNode> rich) {
        String plain = rich.stream().map(node -> node.text).collect(Collectors.joining());
        return convertProvisions(plain, rich);
    }
    private static Effect.Provisions convertProvisions(String plain, List<UnappliedEffect.RichTextNode> rich) {
        Effect.Provisions provisions = new Effect.Provisions();
        provisions.plain = plain;
        provisions.rich = rich.stream().map(EffectsConverter::convertRichTextNode).toList();
        return provisions;
    }

    private static List<Effect.InForce> convertInForceDates(UnappliedEffect clml) {
        return clml.inForceDates.stream().map(EffectsConverter::convertInForceDate).toList();
    }
    private static Effect.InForce convertInForceDate(UnappliedEffect.InForce clml) {
        Effect.InForce inForce = new Effect.InForce();
        inForce.date = clml.date;
        inForce.applied = clml.applied;
        inForce.prospective = clml.prospective;
        inForce.description = clml.qualification;
        return inForce;
    }

    private static Effect.Source makeSource(UnappliedEffect clml) {
        Effect.Source source = new Effect.Source();
        source.id = Links.shorten(clml.affectingURI);
        source.longType = clml.affectingClass;
        source.year = clml.affectingYear;
        source.number = clml.affectingNumber;
        source.provisions = convertProvisions(clml.affectingProvisionsText, clml.affectingProvisions);
        return source;
    }

    private static RichText.Node convertRichTextNode(UnappliedEffect.RichTextNode clml) {
        RichText.Node node = new RichText.Node();
        node.text = clml.text;
        switch (clml.type) {
            case null -> logger.warn("node type is null");
            case UnappliedEffect.RichTextNode.TEXT_TYPE -> node.type = "text";
            case UnappliedEffect.RichTextNode.SECTION_TYPE -> {
                node.type = "link";
                node.id = clml.ref;
                node.href = Links.shorten(clml.uri);
                node.missing = clml.missing ? true : null;
            }
            default -> logger.warn("unrecognized node type: {}", clml.type);
        }
        return node;
    }

}
