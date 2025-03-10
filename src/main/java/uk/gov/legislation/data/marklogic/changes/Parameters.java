package uk.gov.legislation.data.marklogic.changes;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Parameters {

    private String affectedType;
    private Integer affectedYear;
    private Integer affectedNumber;
    private String affectedTitle;

    private String affectingType;
    private Integer affectingYear;
    private Integer affectingNumber;
    private String affectingTitle;

    private String effect;

    private Integer page;
    private Integer resultsCount;

    public static Builder builder() {
        return new Builder();
    }

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

    public static class Builder {

        private final Parameters params = new Parameters();

        private Builder() { }

        public Builder affectedType(String affectedType) {
            params.affectedType = affectedType;
            return this;
        }

        public Builder affectedYear(Integer affectedYear) {
            params.affectedYear = affectedYear;
            return this;
        }

        public Builder affectedNumber(Integer affectedNumber) {
            params.affectedNumber = affectedNumber;
            return this;
        }

        public Builder affectedTitle(String affectedTitle) {
            params.affectedTitle = affectedTitle;
            return this;
        }

        public Builder affectingType(String affectingType) {
            params.affectingType = affectingType;
            return this;
        }

        public Builder affectingYear(Integer affectingYear) {
            params.affectingYear = affectingYear;
            return this;
        }

        public Builder affectingNumber(Integer affectingNumber) {
            params.affectingNumber = affectingNumber;
            return this;
        }

        public Builder affectingTitle(String affectingTitle) {
            params.affectingTitle = affectingTitle;
            return this;
        }

        public Builder type(String type) {
            params.effect = type;
            return this;
        }

        public Builder page(int page) {
            params.page = page;
            return this;
        }

        public Builder pageSize(int size) {
            params.resultsCount = size;
            return this;
        }

        public Parameters build() {
            return params;
        }

    }

    public static Parameters comingIntoForce(String type, int year, int number) {
        return builder()
            .affectedType(type)
            .affectedYear(year)
            .affectedNumber(number)
            .type("coming into force")
            .pageSize(1000)
            .build();
    }

}
