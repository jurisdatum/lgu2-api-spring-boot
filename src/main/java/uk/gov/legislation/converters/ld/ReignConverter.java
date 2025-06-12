package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Reign;
import uk.gov.legislation.data.virtuoso.jsonld.ReignLD;

import static uk.gov.legislation.converters.ld.LDConverter.extractDateAtEndOfUri;
import static uk.gov.legislation.converters.ld.LDConverter.extractIntegerAtEndOfUri;

public class ReignConverter {

    public static Reign convert(ReignLD ld) {
        Reign reign = new Reign();
        reign.uri = ld.id;
        reign.label = ld.label;
        reign.monarchs = ld.monarch.stream().map(LDConverter::extractLastComponentOfUri).toList();
        reign.startDate = extractDateAtEndOfUri(ld.startDate);
        reign.endDate = extractDateAtEndOfUri(ld.endDate);
        reign.startRegnalYear = extractIntegerAtEndOfUri(ld.startRegnalYear);
        reign.endRegnalYear = extractIntegerAtEndOfUri(ld.endRegnalYear);
        return reign;
    }

}
