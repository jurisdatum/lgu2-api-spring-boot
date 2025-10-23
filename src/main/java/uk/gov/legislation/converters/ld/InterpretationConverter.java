package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Interpretation;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.jsonld.InterpretationLD;
import uk.gov.legislation.data.virtuoso.jsonld.Interpretation8LD;

import java.util.Objects;
import java.util.stream.Stream;

public class InterpretationConverter {

    public static Interpretation convert(InterpretationLD ld) {
        Interpretation interpretation = new Interpretation();
        interpretation.uri = ld.id;
        interpretation.language = ld.languageOfTextIsoCode.value;
        interpretation.shortTitle = (ld.shortTitle == null) ? null : ld.shortTitle.value;
        if (interpretation.shortTitle == null && ld.orderTitle != null)
            interpretation.shortTitle = ld.orderTitle.value;
        if (interpretation.shortTitle == null && ld.statuteTitle != null)
            interpretation.shortTitle = ld.statuteTitle.value;
        if (interpretation.shortTitle == null && ld.europeanUnionTitle != null)
            interpretation.shortTitle = ld.europeanUnionTitle.value;
        interpretation.longTitle = (ld.longTitle == null) ? null : ld.longTitle.value;
        if (interpretation.longTitle == null && ld.subjectDescription != null)
            interpretation.longTitle = ld.subjectDescription.value;
        interpretation.original = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.OriginalInterpretation));
        interpretation.current = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.CurrentInterpretation));
        interpretation.parent = ld.within;
        interpretation.children = ld.contains;
        return interpretation;
    }

    public static Interpretation convert(Interpretation8LD ld) {
        Interpretation interpretation = new Interpretation();
        interpretation.uri = ld.id;
        interpretation.language = ld.languageOfTextIsoCode.value;
        interpretation.shortTitle = Stream.of(ld.shortTitle, ld.orderTitle, ld.statuteTitle, ld.europeanUnionTitle)
            .filter(Objects::nonNull)
            .map(title -> title.value)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
        interpretation.longTitle = Stream.of(ld.longTitle, ld.subjectDescription)
            .filter(Objects::nonNull)
            .map(title -> title.value)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
        interpretation.original = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.OriginalInterpretation));
        interpretation.current = ld.type.stream().anyMatch(t -> t.equals(Resources.Leg.CurrentInterpretation));
        interpretation.parent = ld.within;
        interpretation.children = ld.contains;
        interpretation.item = ItemConverter.convert(ld.interpretationOf);
        return interpretation;
    }

}
