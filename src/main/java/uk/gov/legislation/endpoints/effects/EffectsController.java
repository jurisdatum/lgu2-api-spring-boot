package uk.gov.legislation.endpoints.effects;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.api.responses.PageOfEffects;
import uk.gov.legislation.converters.EffectsFeedConverter;
import uk.gov.legislation.data.marklogic.changes.Changes;
import uk.gov.legislation.data.marklogic.changes.Parameters;
import uk.gov.legislation.transform.simple.effects.EffectsSimplifier;
import uk.gov.legislation.transform.simple.effects.Page;

import java.io.IOException;

@RestController
public class EffectsController implements EffectsApi {

    private final Changes db;

    private final EffectsSimplifier simplifier;

    public EffectsController(Changes changes, EffectsSimplifier simplifier) {
        this.db = changes;
        this.simplifier = simplifier;
    }

    public String atom(
        String targetType,
        String targetYear,
        Integer targetNumber,
        String targetTitle,
        String sourceType,
        String sourceYear,
        Integer sourceNumber,
        String sourceTitle,
        int page
    ) throws IOException, InterruptedException {
        Parameters params = Parameters.builder()
            .affectedType(targetType)
            .affectedYear(targetYear)
            .affectedNumber(targetNumber)
            .affectedTitle(targetTitle)
            .affectingType(sourceType)
            .affectingYear(sourceYear)
            .affectingNumber(sourceNumber)
            .affectingTitle(sourceTitle)
            .page(page)
            .build();
        return db.fetch(params);
    }

    public PageOfEffects json(
        String targetType,
        String targetYear,
        Integer targetNumber,
        String targetTitle,
        String sourceType,
        String sourceYear,
        Integer sourceNumber,
        String sourceTitle,
        int page
    ) throws IOException, InterruptedException, SaxonApiException {
        String atom = atom(targetType, targetYear, targetNumber, targetTitle, sourceType, sourceYear, sourceNumber, sourceTitle, page);
        Page simple = simplifier.parse(atom);
        return EffectsFeedConverter.convert(simple);
    }

}
