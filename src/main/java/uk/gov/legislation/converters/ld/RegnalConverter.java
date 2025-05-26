package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Regnal;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalLD;

public class RegnalConverter {
    public static Regnal convert(RegnalLD regnalLD) {
        if (regnalLD == null) return null;

        Regnal regnal = new Regnal();
        regnal.uri = regnalLD.id;
        regnal.type = regnalLD.type;
        regnal.label = regnalLD.label;
        regnal.endYear = regnalLD.getEndCalendarYear();
        regnal.startYear = regnalLD.getStartCalendarYear();
        regnal.yearOfReign = regnalLD.yearOfReign;
        regnal.endDate = regnalLD.getEndDate();
        regnal.reign = regnalLD.reign != null ? regnalLD.reign.toString() : null;
        regnal.overlappingYears = regnalLD.getOverlapsCalendarYear();
        regnal.startDate = regnalLD.getStartDate();

        return regnal;
    }
}

