package uk.gov.legislation.endpoints.document.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.converters.ExtentConverter;
import uk.gov.legislation.endpoints.document.responses.UnappliedEffect;
import uk.gov.legislation.api.responses.RichText;
import uk.gov.legislation.transform.simple.effects.InForce;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.Links;

import java.util.List;
import java.util.stream.Collectors;

public class EffectsConverter {

    private EffectsConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Logger logger = LoggerFactory.getLogger(EffectsConverter.class);

    public static List<UnappliedEffect> convert(List<uk.gov.legislation.transform.simple.effects.Effect> effects) {
        return effects.stream().map(EffectsConverter::convertEffect).toList();
    }

    private static UnappliedEffect convertEffect(uk.gov.legislation.transform.simple.effects.Effect clml) {
        UnappliedEffect effect = new UnappliedEffect();
        effect.type = clml.type;
        effect.required = clml.requiresApplied;
        effect.affected = convertProvisions(clml.affectedProvisionsText, clml.affectedProvisions);
        effect.affectedExtent = ExtentConverter.convert(clml.affectedExtent);
        effect.inForceDates = convertInForceDates(clml);
        effect.source = makeSource(clml);
        effect.commencement = convertProvisions(clml.commencementAuthority);
        effect.notes = clml.notes;
        return effect;
    }

    private static UnappliedEffect.Provisions convertProvisions(List<RichTextNode> rich) {
        String plain = rich.stream().map(node -> node.text).collect(Collectors.joining());
        return convertProvisions(plain, rich);
    }
    private static UnappliedEffect.Provisions convertProvisions(String plain, List<RichTextNode> rich) {
        UnappliedEffect.Provisions provisions = new UnappliedEffect.Provisions();
        provisions.plain = plain;
        provisions.rich = rich.stream().map(EffectsConverter::convertRichTextNode).toList();
        return provisions;
    }

    private static List<UnappliedEffect.InForce> convertInForceDates(uk.gov.legislation.transform.simple.effects.Effect clml) {
        return clml.inForceDates.stream().map(EffectsConverter::convertInForceDate).toList();
    }
    private static UnappliedEffect.InForce convertInForceDate(InForce clml) {
        UnappliedEffect.InForce inForce = new UnappliedEffect.InForce();
        inForce.date = clml.date;
        inForce.applied = clml.applied;
        inForce.prospective = clml.prospective;
        inForce.description = clml.qualification;
        return inForce;
    }

    private static UnappliedEffect.Source makeSource(uk.gov.legislation.transform.simple.effects.Effect clml) {
        UnappliedEffect.Source source = new UnappliedEffect.Source();
        source.id = Links.shorten(clml.affectingURI);
        source.longType = clml.affectingClass;
        source.year = clml.affectingYear;
        source.number = clml.affectingNumber;
        source.cite = Cites.make(source.longType, source.year, source.number);
        source.provisions = convertProvisions(clml.affectingProvisionsText, clml.affectingProvisions);
        return source;
    }

    private static RichText.Node convertRichTextNode(RichTextNode clml) {
        RichText.Node node = new RichText.Node();
        node.text = clml.text;
        switch (clml.type) {
            case null -> logger.warn("node type is null");
            case RichTextNode.TEXT_TYPE -> node.type = "text";
            case RichTextNode.SECTION_TYPE -> {
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
