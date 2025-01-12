package uk.gov.legislation.data.marklogic.changes;

import lombok.Builder;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public class Parameters {

    private String affectedTtitle;
    private String affectedType;
    private Integer affectedNumber;
    private String affectedYear;

    private String affectingTtitle;
    private String affectingType;
    private Integer affectingNumber;
    private String affectingYear;

    private Integer page;

    public String toQuery() {
        Map<String, String> params = new LinkedHashMap<>();
        for (Field field: getClass().getDeclaredFields()) {
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null)
                continue;
            String key = field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
            if (value instanceof String string)
                params.put(key, URLEncoder.encode(string, StandardCharsets.UTF_8));
            else if (value instanceof Integer number)
                params.put(key, number.toString());
        }
        if (params.isEmpty())
            return "";
        return "?" + params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    }

}
