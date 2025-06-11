package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Monarch;
import uk.gov.legislation.data.virtuoso.jsonld.MonarchLD;

public class MonarchConverter {

    public static Monarch convert(MonarchLD ld) {
        Monarch monarch = new Monarch();
        monarch.uri = ld.id;
        monarch.label = ld.label;
        monarch.name = ld.regnalName;
        monarch.number = ld.regnalNumber;
        return monarch;
    }
}
