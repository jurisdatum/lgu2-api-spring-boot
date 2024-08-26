package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.util.List;

@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
public class SearchResults {

    static SearchResults parse(String xml) throws JsonProcessingException {
        ObjectMapper mapper = new XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(new JavaTimeModule());
        return mapper.readValue(xml, SearchResults.class);
    }

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public String id;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public LocalDateTime updated;

    @JacksonXmlProperty(namespace = "http://a9.com/-/spec/opensearch/1.1/")
    public int itemsPerPage;

    @JacksonXmlProperty(namespace = "http://a9.com/-/spec/opensearch/1.1/")
    public int startIndex;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public int page;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public int morePages; // total pages!

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public Facets facets;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "entry")
    public List<Entry> entries;

public static class Facets {

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public FacetTypes facetTypes;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public FacetYears facetYears;

}

public static class FacetTypes {

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public FacetType facetType;

}

public static class FacetType {

    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty(isAttribute = true)
    public int value;

}

public static class FacetYears {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "facetYear", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public List<FacetYear> entries;

}

public static class FacetYear {

    @JacksonXmlProperty(isAttribute = true)
    public int year;

    @JacksonXmlProperty(isAttribute = true)
    public int total;

}

public static class Entry {

    public String id;

    public String title;

    @JacksonXmlProperty(localName = "DocumentMainType", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Value mainType;

    @JacksonXmlProperty(localName = "Year", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Value year;

    @JacksonXmlProperty(localName = "Number", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Value number;

    @JacksonXmlProperty(localName = "CreationDate", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public CreationDate creationDate;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    public List<Link> links;

}

public static class Value {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public String value;

}

public static class CreationDate {

    @JacksonXmlProperty(localName = "Date", isAttribute = true)
    public String date;

}

public static class Link {

    @JacksonXmlProperty(isAttribute = true)
    public String rel;

    @JacksonXmlProperty(isAttribute = true)
    public String type;

    @JacksonXmlProperty(isAttribute = true)
    public String href;

    @JacksonXmlProperty(isAttribute = true)
    public String title;

}

}
