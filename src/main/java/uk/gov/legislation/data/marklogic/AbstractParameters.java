package uk.gov.legislation.data.marklogic;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractParameters {

    public String toQuery() {
        Map<String, String> params = new LinkedHashMap<>();
        for (Field field: getClass().getDeclaredFields()) {
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                value = null;
            }
            if (value == null)
                continue;
            String key = field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
            params.put(key, URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
        }
        if (params.isEmpty())
            return "";
        return "?" + params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    }

}
