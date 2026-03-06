package uk.gov.legislation.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.data.marklogic.changes.Parameters;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.effects.Effect;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;
import uk.gov.legislation.transform.simple.effects.Entry;
import uk.gov.legislation.transform.simple.effects.Page;
import uk.gov.legislation.util.Types;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnappliedEffectsFetcher {

    private static final Logger logger = LoggerFactory.getLogger(UnappliedEffectsFetcher.class);

    private final Changes changes;
    private final EffectsSimplifier simplifier;

    public UnappliedEffectsFetcher(Changes changes, EffectsSimplifier simplifier) {
        this.changes = changes;
        this.simplifier = simplifier;
    }

    /**
     * For enacted/made-only documents (status "final", viewing the latest version),
     * fetches unapplied effects from MarkLogic's changes database and populates
     * {@code simple.rawEffects}. For all other documents, does nothing.
     */
    public void fetchIfNeeded(Metadata simple) {
        if (!"final".equals(simple.status))
            return;
        if (!simple.version().equals(simple.versions().getLast()))
            return;

        String shortType = Types.longToShort(simple.longType);
        if (shortType == null) {
            logger.debug("skipping effects fetch: unknown type {}", simple.longType);
            return;
        }
        if (simple.number == null) {
            logger.debug("skipping effects fetch: no number for {} {}", simple.longType, simple.year);
            return;
        }

        try {
            List<Effect> allEffects = new ArrayList<>();
            Parameters params = Parameters.builder()
                .affectedType(shortType)
                .affectedYear(simple.year)
                .affectedNumber(simple.number)
                .applied(Parameters.AppliedStatus.unapplied)
                .page(1)
                .build();

            String atom = changes.fetch(params);
            Page page = simplifier.parse(atom);
            extractEffects(page, allEffects);

            for (int p = 2; p <= page.totalPages; p++) {
                params = Parameters.builder()
                    .affectedType(shortType)
                    .affectedYear(simple.year)
                    .affectedNumber(simple.number)
                    .applied(Parameters.AppliedStatus.unapplied)
                    .page(p)
                    .build();
                atom = changes.fetch(params);
                page = simplifier.parse(atom);
                extractEffects(page, allEffects);
            }

            simple.rawEffects = allEffects;
            simple.finalEffectsEnriched = true;
            logger.debug("fetched {} unapplied effects for {}/{}/{}", allEffects.size(), shortType, simple.year, simple.number);
        } catch (Exception e) {
            logger.warn("failed to fetch unapplied effects for {}/{}/{}: {}", shortType, simple.year, simple.number, e.getMessage());
        }
    }

    private static void extractEffects(Page page, List<Effect> effects) {
        for (Entry entry : page.entries) {
            if (entry.content != null && entry.content.effect != null)
                effects.add(entry.content.effect);
        }
    }

}
