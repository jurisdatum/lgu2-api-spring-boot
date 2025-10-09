package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

@JacksonXmlRootElement
@JsonIgnoreProperties(ignoreUnknown = false)
public class Error {

    public static Error parse(String xml) throws JsonProcessingException {
        Error error = READER.readValue(xml);
        validate(error);
        return error;
    }

    private static final XmlMapper MAPPER = (XmlMapper) new XmlMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    private static final ObjectReader READER = MAPPER.readerFor(Error.class);

    @JacksonXmlProperty(localName = "status-code")
    public int statusCode;

    public String message;

    public Header header;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    public List<Link> links;

    private static void validate(Error error) {
        if (error.statusCode == 0) {
            throw new IllegalArgumentException("MarkLogic error response missing status code");
        }
        if (error.statusCode >= 300 && error.statusCode < 400) {
            if (error.header == null) {
                throw new IllegalArgumentException("MarkLogic redirect response missing header");
            }
            if (error.header.name == null || error.header.name.isEmpty()) {
                throw new IllegalArgumentException("MarkLogic redirect header missing name");
            }
            if (error.header.value == null || error.header.value.isEmpty()) {
                throw new IllegalArgumentException("MarkLogic redirect header missing value");
            }
        }
    }

    public static class Header {

        public String name;

        public String value;

    }

    public static class Link {

        @JacksonXmlProperty(isAttribute = true)
        public String href;

        @JacksonXmlText
        public String text;

    }

}
