package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Reign;
import uk.gov.legislation.data.virtuoso.jsonld.ReignLD;

import java.util.ArrayList;

public class ReignConverter {

    public static Reign convertToReign(ReignLD reignLD) {
        if (reignLD == null) {
            return null;
        }

        Reign reign = new Reign();

        reign.uri = reignLD.id;
        reign.type = (reignLD.type != null) ? reignLD.type : "Reign";
        reign.label = reignLD.label;
        reign.endYear = reignLD.endCalendarYear;
        reign.endRegnalYear = reignLD.endRegnalYear;
        reign.startYear = reignLD.startCalendarYear;
        reign.startRegnalYear = reignLD.startRegnalYear;
        reign.endDate = reignLD.endDate;
        reign.monarch = reignLD.monarch;
        reign.overlappingYears = (reignLD.overlapsCalendarYear != null) ? reignLD.overlapsCalendarYear : new ArrayList<>();
        reign.overlappingRegnalYears = (reignLD.overlapsRegnalYear != null) ? reignLD.overlapsRegnalYear : new ArrayList<>();
        reign.startDate = reignLD.startDate;

        return reign;
    }
}
