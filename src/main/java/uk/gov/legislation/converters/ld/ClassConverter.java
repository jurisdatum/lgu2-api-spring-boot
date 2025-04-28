package uk.gov.legislation.converters.ld;

import org.slf4j.LoggerFactory;
import uk.gov.legislation.api.responses.ld.Clazz;
import uk.gov.legislation.data.virtuoso.jsonld.ClassLD;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClassConverter {

    public static Clazz convert(ClassLD ld) {
        Clazz clazz = new Clazz();
        clazz.uri = ld.id;
        clazz.other = new HashMap<>(ld.other);
        oneOrMany(clazz, "subClassOf");
        return clazz;
    }

    private static boolean filter(String uri) {
        return uri.startsWith("http://www.legislation.gov.uk/def/legislation/");
    }

    private static String trim(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

    private static void oneOrMany(Clazz clazz, String key) {
        Object value = clazz.other.get(key);
        switch (value) {
            case String one -> {
                if (filter(one))
                    clazz.other.put(key, Collections.singletonList(trim(one)));
                else
                    clazz.other.remove(key);
            }
            case List<?> multiple -> {
                multiple = multiple.stream().map(String.class::cast)
                    .filter(ClassConverter::filter)
                    .map(ClassConverter::trim)
                    .toList();
                if (multiple.isEmpty())
                    clazz.other.remove(key);
                else
                    clazz.other.put(key, multiple);
            }
            default -> LoggerFactory.getLogger(ClassConverter.class).warn("unexpected type: {}", value.getClass().getName());
        }
    }

}
