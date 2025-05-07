package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Monarch;
import uk.gov.legislation.data.virtuoso.jsonld.MonarchLD;

public final class MonarchConverter {
    private MonarchConverter() {}

    public static Monarch convert(MonarchLD ld) {
        return new Monarch(
            ld.id(),
            ld.type(),
            ld.label(),
            ld.regnalName(),
            ld.regnalNumber()
        );
    }
}
