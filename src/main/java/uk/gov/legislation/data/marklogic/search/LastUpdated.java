package uk.gov.legislation.data.marklogic.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.ZonedDateTime;

@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
public class LastUpdated {

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public ZonedDateTime updated;

    public static ZonedDateTime get(String atom) throws JsonProcessingException {
        LastUpdated parsed = SearchResults.MAPPER.readValue(atom, LastUpdated.class);
        return parsed.updated;
    }

}
