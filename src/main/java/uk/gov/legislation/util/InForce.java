package uk.gov.legislation.util;

import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.transform.simple.effects.Effect;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InForce {

    public static void addInForceDates(TableOfContents toc, List<Effect> effects) {
        Map<String, Effect> map = effects.stream()
            .collect(Collectors.toMap(e -> e.id, Function.identity()));
        addInForceDates(toc.contents.body, map);
    }

    private static void addInForceDates(List<TableOfContents. Item> items, Map<String, Effect> map) {
        if (items == null)
            return;
        for (TableOfContents.Item item: items) {
            String id = item.ref;
            Iterator<Map.Entry<String, Effect>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Effect> entry = iterator.next();
                Effect effect = entry.getValue();
                if (!Effects.relatesTo(effect, id))
                    continue;
                effect.inForceDates.stream()
                    .filter(inForce -> inForce.date != null)
                    .map(inForce -> inForce.date)
                    .findFirst()
                    .ifPresent(date -> item.inForce = date);
//                iterator.remove();
                break;
            }
            addInForceDates(item.children, map);
        }
    }

}
