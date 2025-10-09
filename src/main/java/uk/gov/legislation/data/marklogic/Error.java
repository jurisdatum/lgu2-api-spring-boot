package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.util.List;

@JacksonXmlRootElement()
public class Error {

    public static Error parse(String xml) throws JsonProcessingException {
        return MAPPER.readValue(xml, Error.class);
    }

    private static final XmlMapper MAPPER = (XmlMapper) new XmlMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @JacksonXmlProperty(localName = "status-code")
    public int statusCode;

    public String message;

    public Header header;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    public List<Link> links;

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
