package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement()
public class Error {

    static Error parse(String xml) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(xml, Error.class);
    }

    @JacksonXmlProperty(localName = "status-code")
    public int statusCode;

    public String message;

    public Header header;

    public static class Header {

        public String name;

        public String value;

    }

}
