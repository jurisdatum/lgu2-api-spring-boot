package uk.gov.legislation.data.virtuoso;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class JsonResults {

    public static JsonResults parse(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, JsonResults.class);
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
