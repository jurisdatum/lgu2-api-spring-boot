package uk.gov.legislation.data.marklogic.search;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.core.JacksonException;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.ZonedDateTime;

@JsonRootName(value = "feed", namespace = "http://www.w3.org/2005/Atom")
public class LastUpdated {

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public ZonedDateTime updated;

    public static ZonedDateTime get(String atom) throws JacksonException {
        LastUpdated parsed = SearchResults.MAPPER.readValue(atom, LastUpdated.class);
        return parsed.updated;
    }

}
