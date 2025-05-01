package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ValueAndLanguage {

    @JsonProperty("@value")
    public String value;

    @JsonProperty("@language")
    public String language;

    public static String get(List<ValueAndLanguage> list, String language) {
        return list.stream()
            .filter(x -> language.equals(x.language))
            .findFirst()
            .map(x -> x.value)
            .orElse(null);
    }

}
