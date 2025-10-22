package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.api.responses.LabelledLink;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.util.Effects;
import uk.gov.legislation.util.EffectsComparator;
import uk.gov.legislation.util.Links;
import uk.gov.legislation.util.UpToDate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FragmentMetadataConverter {

    /**
     * Converts the simplified transform model into the public API response.
     *
     * <p>For {@code prevInfo}/{@code nextInfo}, the {@code href} is a shortened
     * path derived from the corresponding URI and the {@code label} is taken
     * from the first component of the Atom {@code link/@title} attribute in the
     * source XML when present. The upstream title value is semicolon‑separated
     * and may include additional components that are currently ignored.</p>
     */
    public static FragmentMetadata convert(Metadata simple) {
        FragmentMetadata converted = new FragmentMetadata();
        DocumentMetadataConverter.convert(simple, converted);
        converted.fragment = simple.fragment();
        converted.prev = simple.prev();
        converted.next = simple.next();
        if (simple.prevUri != null) {
            converted.prevInfo = new LabelledLink();
            converted.prevInfo.href = Links.extractFragmentIdentifierFromLink(simple.prevUri);
            if (simple.prevTitle != null) {
                // Split Atom link/@title on ';' and use the first component; see LabelledLink.
                String[] titleParts = simple.prevTitle.split(";", 2);
                converted.prevInfo.label = titleParts[0].trim();
                // TODO: consider exposing remaining components after the first ';'.
            }
        }
        if (simple.nextUri != null) {
            converted.nextInfo = new LabelledLink();
            converted.nextInfo.href = Links.extractFragmentIdentifierFromLink(simple.nextUri);
            if (simple.nextTitle != null) {
                // Same as prev; see LabelledLink.
                String[] titleParts = simple.nextTitle.split(";", 2);
                converted.nextInfo.label = titleParts[0].trim();
                // TODO: consider exposing remaining components after the first ';'.
            }
        }
        converted.ancestors = simple.ancestors();
        converted.descendants = simple.descendants();
        converted.fragmentInfo = converted.descendants.getFirst();
        converted.unappliedEffects = convertEffects(simple);
        if ("revised".equals(simple.status) && simple.version().equals(simple.versions().getLast())) {
            if (converted.pointInTime == null)
                UpToDate.setUpToDate(converted);
            else
                UpToDate.setUpToDate(converted, converted.pointInTime);
        }
        return converted;
    }

    private static FragmentMetadata.Effects convertEffects(Metadata metadata) {
        Set<String> descendantIds = metadata.descendants().stream()
            .map(level -> level.id)
            .collect(Collectors.toSet());
        Set<String> ancestorIds = metadata.ancestors().stream()
            .map(level -> level.id)
            .collect(Collectors.toSet());
        String fragmentId = metadata.descendants().stream().findFirst().map(level -> level.id).orElse(null);
        ancestorIds.remove(fragmentId);

        List<Effect> all = metadata.rawEffects.stream().sorted(EffectsComparator.INSTANCE).toList();
        List<Effect> direct = Effects.removeThoseWithNoRelevantSection(all, descendantIds, false);
        List<Effect> ancestor = Effects.removeThoseWithNoRelevantSection(all, ancestorIds, true);
        FragmentMetadata.Effects effects = new FragmentMetadata.Effects();
        effects.fragment = EffectsFeedConverter.convertEffects(direct);
        effects.ancestor = EffectsFeedConverter.convertEffects(ancestor);
        return effects;
    }

}
