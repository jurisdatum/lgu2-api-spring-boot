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

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;
import static uk.gov.legislation.endpoints.search.SearchController.validateYears;

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
        Integer targetYear,
        Integer targetNumber,
        Integer targetStartYear,
        Integer targetEndYear,
        String targetTitle,
        String sourceType,
        Integer sourceYear,
        Integer sourceNumber,
        Integer sourceStartYear,
        Integer sourceEndYear,
        String sourceTitle,
        Parameters.AppliedStatus appliedStatus,
        int page
    ) throws IOException, InterruptedException {
        validateType(targetType);
        validateYears(targetYear, targetStartYear, targetEndYear);
        validateType(sourceType);
        validateYears(sourceYear, sourceStartYear, sourceEndYear);
        Parameters params = Parameters.builder()
            .affectedType(targetType)
            .affectedYear(targetYear)
            .affectedNumber(targetNumber)
            .affectedStartYear(targetStartYear)
            .affectedEndYear(targetEndYear)
            .affectedTitle(targetTitle)
            .affectingType(sourceType)
            .affectingYear(sourceYear)
            .affectingNumber(sourceNumber)
            .affectingStartYear(sourceStartYear)
            .affectingEndYear(sourceEndYear)
            .affectingTitle(sourceTitle)
            .applied(appliedStatus)
            .page(page)
            .build();
        return db.fetch(params);
    }


    public PageOfEffects json(
        String targetType,
        Integer targetYear,
        Integer targetNumber,
        Integer targetStartYear,
        Integer targetEndYear,
        String targetTitle,
        String sourceType,
        Integer sourceYear,
        Integer sourceNumber,
        Integer sourceStartYear,
        Integer sourceEndYear,
        String sourceTitle,
        Parameters.AppliedStatus appliedStatus,
        int page
    ) throws IOException, InterruptedException, SaxonApiException {
        validateType(targetType);
        validateYears(targetYear, targetStartYear, targetEndYear);
        validateType(sourceType);
        validateYears(sourceYear, sourceStartYear, sourceEndYear);
        String atom = atom(targetType, targetYear,
            targetNumber,targetStartYear,
            targetEndYear, targetTitle,
            sourceType, sourceYear,
            sourceNumber,sourceStartYear,
            sourceEndYear, sourceTitle,
            appliedStatus, page);
        Page simple = simplifier.parse(atom);
        return EffectsFeedConverter.convert(simple);
    }

}
