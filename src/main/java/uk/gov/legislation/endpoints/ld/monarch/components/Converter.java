package uk.gov.legislation.endpoints.ld.monarch.components;


import uk.gov.legislation.endpoints.ld.monarch.response.Monarch;
import uk.gov.legislation.endpoints.ld.monarch.response.MonarchLD;

import java.util.function.Function;

public interface Converter extends Function<MonarchLD, Monarch> {

    Converter DEFAULT = ld -> {
        Monarch monarch = new Monarch();
        monarch.uri = ld.id;
        monarch.type = ld.type;
        monarch.label = ld.label;
        monarch.name = ld.regnalName;
        monarch.number = ld.regnalNumber;
        return monarch;
    };
}
