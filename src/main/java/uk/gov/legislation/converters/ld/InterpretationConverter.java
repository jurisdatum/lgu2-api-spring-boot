package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.jsonld.InterpretationLD;

public class InterpretationConverter {

    public static Interpretation convert(InterpretationLD ld) {
        Interpretation interpretation = new Interpretation();
        interpretation.uri = ld.id;
        interpretation.language = ld.languageOfTextIsoCode.value;
        interpretation.longTitle = (ld.longTitle == null) ? null : ld.longTitle.value;
        interpretation.shortTitle = (ld.shortTitle == null) ? null : ld.shortTitle.value;
        interpretation.orderTitle = (ld.orderTitle == null) ? null : ld.orderTitle.value;
        interpretation.original = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.OriginalInterpretation));
        interpretation.current = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.CurrentInterpretation));
        return interpretation;
    }

}
