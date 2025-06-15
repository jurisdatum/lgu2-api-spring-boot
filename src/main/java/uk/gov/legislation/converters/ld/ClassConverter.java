package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.ClassResponse;
import uk.gov.legislation.data.virtuoso.jsonld.ClassLD;

import java.util.LinkedHashMap;

public class ClassConverter {

    public static ClassResponse convert(ClassLD ld) {
        ClassResponse clazz = new ClassResponse();
        clazz.uri = ld.id;
        clazz.other = new LinkedHashMap<>(ld.other);
        return clazz;
    }

}
