package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Legislature;
import uk.gov.legislation.data.virtuoso.jsonld.LegislatureLD;

public class LegislatureConverter {

    public static Legislature convert(LegislatureLD ld) {
        Legislature legislature = new Legislature();
        legislature.uri = ld.id;
        legislature.type = ld.type.substring(46);
        return legislature;
    }

}
