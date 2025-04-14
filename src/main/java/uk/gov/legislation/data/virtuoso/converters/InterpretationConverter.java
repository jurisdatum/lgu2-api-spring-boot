package uk.gov.legislation.data.virtuoso.converters;

import uk.gov.legislation.data.virtuoso.jsonld.InterpretationLD;
import uk.gov.legislation.data.virtuoso.model.Resources;
import uk.gov.legislation.data.virtuoso.model2.Interpretation;

import java.net.URI;

public class InterpretationConverter {

    public static Interpretation convert(InterpretationLD ld) {
        Interpretation interpretation = new Interpretation();
        interpretation.uri = URI.create(ld.id);
        interpretation.language = ld.languageOfTextIsoCode.value;
        interpretation.longTitle = ld.longTitle.value;
        interpretation.shortTitle = ld.shortTitle.value;
        interpretation.original = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.OriginalInterpretation));
        interpretation.current = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.CurrentInterpretation));
        return interpretation;
    }

}
