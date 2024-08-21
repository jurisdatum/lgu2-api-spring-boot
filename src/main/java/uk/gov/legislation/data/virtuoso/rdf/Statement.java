package uk.gov.legislation.data.virtuoso.rdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Statement(URI subject, URI predicate, TypedValue object) {

    public static Map<URI, Map<URI, List<TypedValue>>> groupBySubjectAndPredicate(List<Statement> tripes) {
        LinkedHashMap<URI, Map<URI, List<TypedValue>>> map1 = new LinkedHashMap<>();
        for (Statement triple: tripes) {
            if (!map1.containsKey(triple.subject))
                map1.put(triple.subject, new LinkedHashMap<>());
            Map<URI, List<TypedValue>> map2 = map1.get(triple.subject);
            if (!map2.containsKey(triple.predicate))
                map2.put(triple.predicate, new ArrayList<>());
            List<TypedValue> list = map2.get(triple.predicate);
            list.add(triple.object);
        }
        return map1;
    }

}
