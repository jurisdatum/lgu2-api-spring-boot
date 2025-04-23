package uk.gov.legislation.data.marklogic.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.ZonedDateTime;

@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
public class LastUpdated {

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public ZonedDateTime updated;

    public static ZonedDateTime get(String atom) throws JsonProcessingException {
        ObjectMapper mapper = new XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(new JavaTimeModule());
        LastUpdated parsed = mapper.readValue(atom, LastUpdated.class);
        return parsed.updated;
    }

}
