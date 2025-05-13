package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Regnal;
import uk.gov.legislation.data.virtuoso.jsonld.RegnalLD;

import java.util.ArrayList;



public class RegnalConverter {

    public static Regnal convertToRegnal(RegnalLD regnalLD) {
        if (regnalLD == null) {
            return null;
        }

        Regnal regnal = new Regnal();

        regnal.uri = regnalLD.id;
        regnal.type = (regnalLD.type != null) ? regnalLD.type : "RegnalYear";
        regnal.label = regnalLD.label;
        regnal.endYear = regnalLD.endCalendarYear;
        regnal.startYear = regnalLD.startCalendarYear;
        regnal.yearOfReign = regnalLD.yearOfReign;
        regnal.endDate = regnalLD.endDate;
        regnal.reign = regnalLD.reign;
        regnal.overlappingYears = (regnalLD.overlapsCalendarYear != null) ? regnalLD.overlapsCalendarYear : new ArrayList <>();
        regnal.startDate = regnalLD.startDate;

        return regnal;
    }
}

