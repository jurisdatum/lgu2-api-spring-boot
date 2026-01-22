package uk.gov.legislation.data.virtuoso;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class JsonResults {

    public static final JsonMapper MAPPER = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

    public static JsonResults parse(String json) throws JacksonException {
        return MAPPER.readValue(json, JsonResults.class);
    }

    public Results results;

    public static class Results {

        public boolean distinct;

        public boolean ordered;

        public List<Map<String, Value>> bindings;

    }

    public static class Value implements TypedValue {

        public String value;

        public String type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public URI datatype;

        @JsonProperty(value = "xml:lang")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String lang;

        @Override
        public String value() {
            return value;
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public URI datatype() {
            return datatype;
        }

        @Override
        public String lang() {
            return lang;
        }
    }

}
