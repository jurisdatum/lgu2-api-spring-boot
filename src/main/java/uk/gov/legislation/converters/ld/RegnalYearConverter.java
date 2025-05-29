package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.RegnalYear;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalYearLD;

import static uk.gov.legislation.converters.ld.LDConverter.extractDateAtEndOfUri;
import static uk.gov.legislation.converters.ld.LDConverter.extractLastComponentOfUri;

public class RegnalYearConverter {
    public static RegnalYear convert(RegnalYearLD regnalLD) {
        if (regnalLD == null)
            return null;
        RegnalYear regnal = new RegnalYear();
        regnal.uri = regnalLD.id;
        regnal.label = regnalLD.label;
        regnal.yearOfReign = regnalLD.yearOfReign;
        regnal.reign = extractLastComponentOfUri(regnalLD.reign);
        regnal.startDate = extractDateAtEndOfUri(regnalLD.startDate);
        regnal.endDate = extractDateAtEndOfUri(regnalLD.endDate);
        return regnal;
    }

}
