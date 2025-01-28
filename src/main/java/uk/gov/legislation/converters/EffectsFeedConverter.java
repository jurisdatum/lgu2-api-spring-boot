package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.Effect;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.transform.simple.effects.Entry;
import uk.gov.legislation.transform.simple.effects.InForce;
import uk.gov.legislation.transform.simple.effects.Page;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.Links;

import java.util.List;
import java.util.stream.Collectors;

public class EffectsFeedConverter {

    public static PageOfEffects convert(Page atom) {
        PageOfEffects page = new PageOfEffects();
        page.meta = convertMetadata(atom);
        page.effects = convertEntries(atom.entries);
        return page;
    }

    private static PageOfEffects.PageMetadata convertMetadata(Page atom) {
        PageOfEffects.PageMetadata meta = new PageOfEffects.PageMetadata();
        meta.page = atom.page;
        meta.pageSize = atom.itemsPerPage;
        meta.totalPages = atom.totalPages;
        meta.startIndex = atom.startIndex;
        meta.totalResults = atom.totalResults;
        meta.updated = atom.updated;
        return meta;
    }

    private static List<Effect> convertEntries(List<Entry> entries) {
        return entries.stream().map(EffectsFeedConverter::convertEntry).toList();
    }

    private static Effect convertEntry(Entry entry) {
        uk.gov.legislation.transform.simple.effects.Effect simple = entry.content.effect;
        return convertEffect(simple);
    }

    public static List<Effect> convertEffects(List<uk.gov.legislation.transform.simple.effects.Effect> simple) {
        return simple.stream().map(EffectsFeedConverter::convertEffect).toList();
    }

    public static Effect convertEffect(uk.gov.legislation.transform.simple.effects.Effect simple) {

        Effect effect = new Effect();
        effect.applied = simple.applied;
        effect.required = simple.requiresApplied;
        effect.type = simple.type;

        effect.target = new Effect.Source();
        effect.target.id = Links.shorten(simple.affectedURI);
        effect.target.longType = simple.affectedClass;
        effect.target.year = simple.affectedYear;
        effect.target.number = simple.affectedNumber;
        effect.target.title = simple.affectedTitle;
        effect.target.cite = Cites.make(simple.affectedClass, simple.affectedYear, simple.affectedNumber);
        effect.target.provisions = new Effect.Provisions();
        effect.target.provisions.plain = simple.affectedProvisionsText;
        effect.target.provisions.rich = simple.affectedProvisions.stream().map(RichTextConverter::convert).toList();
        effect.target.extent = ExtentConverter.convert(simple.affectedExtent);

        effect.source = new Effect.Source();
        effect.source.id = Links.shorten(simple.affectingURI);
        effect.source.longType = simple.affectingClass;
        effect.source.year = simple.affectingYear;
        effect.source.number = simple.affectingNumber;
        effect.source.title = simple.affectingTitle;
        effect.source.cite = Cites.make(simple.affectingClass, simple.affectingYear, simple.affectingNumber);
        effect.source.provisions = new Effect.Provisions();
        effect.source.provisions.plain = simple.affectingProvisionsText;
        effect.source.provisions.rich = simple.affectingProvisions.stream().map(RichTextConverter::convert).toList();
        effect.source.extent = ExtentConverter.convert(simple.affectingExtent);

        if (!simple.commencementAuthority.isEmpty()) {
            effect.commencementAuthority = new Effect.Provisions();
            effect.commencementAuthority.rich = simple.commencementAuthority.stream().map(RichTextConverter::convert).toList();
            effect.commencementAuthority.plain = effect.commencementAuthority.rich.stream().map(node -> node.text).collect(Collectors.joining());
        }

        effect.inForceDates= simple.inForceDates.stream().map(EffectsFeedConverter::convertInForceDate).toList();

        return effect;
    }

    private static Effect.InForce convertInForceDate(InForce clml) {
        Effect.InForce inForce = new Effect.InForce();
        inForce.date = clml.date;
        inForce.applied = clml.applied;
        inForce.prospective = clml.prospective;
        inForce.description = clml.qualification;
        return inForce;
    }

}
