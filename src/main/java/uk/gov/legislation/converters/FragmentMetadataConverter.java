package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.endpoints.document.service.EffectsConverter;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.Effects;
import uk.gov.legislation.util.EffectsComparator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FragmentMetadataConverter {

    public static FragmentMetadata convert(Metadata simple) {
        FragmentMetadata converted = new FragmentMetadata();
        DocumentMetadataConverter.convert(simple, converted);
        converted.fragment = simple.fragment(); // .replace('/', '-')
        converted.prev = simple.prev();
        converted.next = simple.next();
        converted.ancestors = simple.ancestors();
        converted.descendants = simple.descendants();
        converted.unappliedEffects = convertEffects(simple);
        return converted;
    }

    private static FragmentMetadata.Effects convertEffects(Metadata metadata) {
        Set<String> descendantIds = metadata.descendants().stream()
            .map(level -> level.id)
            .collect(Collectors.toSet());
        Set<String> ancestorIds = metadata.ancestors().stream()
            .map(level -> level.id)
            .filter(id -> !id.equals(metadata.fragment()))
            .collect(Collectors.toSet());
        List<Effect> all = metadata.rawEffects.stream().sorted(EffectsComparator.INSTANCE).toList();
        List<Effect> direct = Effects.removeThoseWithNoRelevantSection(all, descendantIds, false);
        List<Effect> ancestor = Effects.removeThoseWithNoRelevantSection(all, ancestorIds, true);
        FragmentMetadata.Effects effects = new FragmentMetadata.Effects();
        effects.fragment = EffectsConverter.convert(direct);
        effects.ancestor = EffectsConverter.convert(ancestor);
        return effects;
    }

}
