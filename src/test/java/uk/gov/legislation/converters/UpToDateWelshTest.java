package uk.gov.legislation.converters;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.api.responses.Effect;
import uk.gov.legislation.util.UpToDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the Welsh simplification in the converter correctly overwrites
 * applied/required with Welsh-specific values, so that UpToDate produces
 * the correct result for Welsh documents.
 */
class UpToDateWelshTest {

    private static final LocalDate CUTOFF = LocalDate.of(2025, 6, 1);

    /**
     * An effect that is applied in English but not in Welsh.
     * An English document should be up to date; a Welsh document should not.
     */
    @Test
    void welshDocumentUsesWelshAppliedField() {
        Effect effect = makeEffect();
        effect.applied = true;
        effect.appliedWelsh = false;
        effect.required = true;
        effect.requiredWelsh = true;

        DocumentMetadata english = makeDocument("en", effect);
        UpToDate.setUpToDate(english, CUTOFF);
        assertTrue(english.upToDate, "English document should be up to date (effect applied in English)");

        Effect welshEffect = makeEffect();
        welshEffect.applied = true;
        welshEffect.appliedWelsh = false;
        welshEffect.required = true;
        welshEffect.requiredWelsh = true;
        DocumentMetadata welsh = makeDocument("cy", welshEffect);
        DocumentMetadataConverter.simplifyWelshEffects(welsh.unappliedEffects);
        UpToDate.setUpToDate(welsh, CUTOFF);
        assertFalse(welsh.upToDate, "Welsh document should NOT be up to date (effect not applied in Welsh)");
    }

    /**
     * An effect that is required in English but not in Welsh.
     * An English document should not be up to date; a Welsh document should.
     */
    @Test
    void welshDocumentUsesWelshRequiredField() {
        Effect effect = makeEffect();
        effect.applied = false;
        effect.appliedWelsh = false;
        effect.required = true;
        effect.requiredWelsh = false;

        DocumentMetadata english = makeDocument("en", effect);
        UpToDate.setUpToDate(english, CUTOFF);
        assertFalse(english.upToDate, "English document should NOT be up to date (effect is required)");

        Effect welshEffect = makeEffect();
        welshEffect.applied = false;
        welshEffect.appliedWelsh = false;
        welshEffect.required = true;
        welshEffect.requiredWelsh = false;
        DocumentMetadata welsh = makeDocument("cy", welshEffect);
        DocumentMetadataConverter.simplifyWelshEffects(welsh.unappliedEffects);
        UpToDate.setUpToDate(welsh, CUTOFF);
        assertTrue(welsh.upToDate, "Welsh document should be up to date (effect not required in Welsh)");
    }

    /**
     * An effect that is not applied in English but is applied in Welsh.
     * An English document should not be up to date; a Welsh document should.
     */
    @Test
    void englishDocumentUsesEnglishAppliedField() {
        Effect effect = makeEffect();
        effect.applied = false;
        effect.appliedWelsh = true;
        effect.required = true;
        effect.requiredWelsh = true;

        DocumentMetadata english = makeDocument("en", effect);
        UpToDate.setUpToDate(english, CUTOFF);
        assertFalse(english.upToDate, "English document should NOT be up to date (effect not applied in English)");

        Effect welshEffect = makeEffect();
        welshEffect.applied = false;
        welshEffect.appliedWelsh = true;
        welshEffect.required = true;
        welshEffect.requiredWelsh = true;
        DocumentMetadata welsh = makeDocument("cy", welshEffect);
        DocumentMetadataConverter.simplifyWelshEffects(welsh.unappliedEffects);
        UpToDate.setUpToDate(welsh, CUTOFF);
        assertTrue(welsh.upToDate, "Welsh document should be up to date (effect applied in Welsh)");
    }

    /**
     * An effect that is not required in English but is required in Welsh.
     * An English document should be up to date; a Welsh document should not.
     */
    @Test
    void englishDocumentUsesEnglishRequiredField() {
        Effect effect = makeEffect();
        effect.applied = false;
        effect.appliedWelsh = false;
        effect.required = false;
        effect.requiredWelsh = true;

        DocumentMetadata english = makeDocument("en", effect);
        UpToDate.setUpToDate(english, CUTOFF);
        assertTrue(english.upToDate, "English document should be up to date (effect not required in English)");

        Effect welshEffect = makeEffect();
        welshEffect.applied = false;
        welshEffect.appliedWelsh = false;
        welshEffect.required = false;
        welshEffect.requiredWelsh = true;
        DocumentMetadata welsh = makeDocument("cy", welshEffect);
        DocumentMetadataConverter.simplifyWelshEffects(welsh.unappliedEffects);
        UpToDate.setUpToDate(welsh, CUTOFF);
        assertFalse(welsh.upToDate, "Welsh document should NOT be up to date (effect is required in Welsh)");
    }

    private static DocumentMetadata makeDocument(String lang, Effect effect) {
        DocumentMetadata meta = new DocumentMetadata();
        meta.lang = lang;
        meta.unappliedEffects = new ArrayList<>(List.of(effect));
        return meta;
    }

    private static Effect makeEffect() {
        Effect effect = new Effect();
        Effect.InForce inForce = new Effect.InForce();
        inForce.date = LocalDate.of(2025, 1, 1); // before cutoff
        effect.inForce = List.of(inForce);
        return effect;
    }
}
