package uk.gov.legislation.data.marklogic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractParameters {

    private static final Pattern CAMEL_TO_LISP_CASE = Pattern.compile("([a-z])([A-Z])");

    String makeKey(Field field) {
        return CAMEL_TO_LISP_CASE
            .matcher(field.getName())
            .replaceAll("$1-$2")
            .toLowerCase();
    }

    public String toQuery() {
        Map<String, String> params = new LinkedHashMap<>();
        for (Field field: getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;
            if (field.isSynthetic())
                continue;
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null)
                continue;
            String key = makeKey(field);
            params.put(key, URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
        }
        if (params.isEmpty())
            return "";
        return "?" + params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    }

    @Override
    public String toString() { return toQuery(); }

}
