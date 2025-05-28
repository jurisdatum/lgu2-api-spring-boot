package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Regnal;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalLD;

import static uk.gov.legislation.converters.ld.LDConverter.extractDateAtEndOfUri;
import static uk.gov.legislation.converters.ld.LDConverter.extractLastComponentOfUri;

public class RegnalConverter {
    public static Regnal convert(RegnalLD regnalLD) {
        if (regnalLD == null)
            return null;
        Regnal regnal = new Regnal();
        regnal.uri = regnalLD.id;
        regnal.label = regnalLD.label;
        regnal.yearOfReign = regnalLD.yearOfReign;
        regnal.reign = extractLastComponentOfUri(regnalLD.reign);
        regnal.startDate = extractDateAtEndOfUri(regnalLD.startDate);
        regnal.endDate = extractDateAtEndOfUri(regnalLD.endDate);
        return regnal;
    }

}
