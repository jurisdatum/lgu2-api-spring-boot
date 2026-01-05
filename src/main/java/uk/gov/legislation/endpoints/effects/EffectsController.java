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

    public String atom(EffectsParameters param) throws IOException, InterruptedException {
        validateType(param.getTargetType());
        validateYears(param.getTargetYear(), param.getTargetStartYear(), param.getTargetEndYear());
        validateType(param.getSourceType());
        validateYears(param.getSourceYear(), param.getSourceStartYear(), param.getSourceEndYear());
        Parameters params = Parameters.builder()
            .affectedType(param.getTargetType())
            .affectedYear(param.getTargetYear())
            .affectedNumber(param.getTargetNumber())
            .affectedStartYear(param.getTargetStartYear())
            .affectedEndYear(param.getTargetEndYear())
            .affectedTitle(param.getTargetTitle())
            .affectingType(param.getSourceType())
            .affectingYear(param.getSourceYear())
            .affectingNumber(param.getSourceNumber())
            .affectingStartYear(param.getSourceStartYear())
            .affectingEndYear(param.getSourceEndYear())
            .affectingTitle(param.getSourceTitle())
            .applied(param.getApplied())
            .page(param.getPage())
            .build();
        return db.fetch(params);
    }


    public PageOfEffects json(EffectsParameters param) throws IOException, InterruptedException, SaxonApiException {
        // parameter validation done in atom() method
        String atom = atom(param);
        Page simple = simplifier.parse(atom);
        return EffectsFeedConverter.convert(simple);
    }

}
