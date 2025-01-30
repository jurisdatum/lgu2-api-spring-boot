package uk.gov.legislation.data.marklogic.search;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
public class SearchResults {

    public static SearchResults parse(String xml) throws JsonProcessingException {
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

    @JacksonXmlProperty(localName = "facetSubjectsInitials", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public Subjects subjects;

}

public static class FacetTypes {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "facetType", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public List<FacetType> entries;

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

public static class Subjects {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "facetSubjectInitial", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public List<SubjectInitial> initials;

    @JacksonXmlElementWrapper(localName = "headings", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    @JacksonXmlProperty(localName = "heading", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public List<SubjectHeading> headings;

}

public static class SubjectInitial {

    @JacksonXmlProperty(isAttribute = true)
    public String initial;

    @JacksonXmlProperty(isAttribute = true)
    public int total;

}

public static class SubjectHeading {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public String name;

}

public static class Entry {

    public String id;

    public String title;

    public String altTitle;

    @SuppressWarnings("unchecked")
    @JsonSetter("title")
    public void setTitle(Object obj) {
        if (obj instanceof String str) {
            title = str;
            return;
        }
        Map<String, ?> map = (Map<String, ?>) obj;
        Map<String, ?> div = (Map<String, ?>) map.get("div");
        ArrayList<Map<String, String>> spans = (ArrayList<Map<String, String>>) div.get("span");
        for (Map<String, String> span : spans) {
            String lang = span.get("lang");
            String title = span.get("");
            if ("en".equals(lang))
                this.title = title;
            else
                this.altTitle = title;
        }
    }

    @JacksonXmlProperty(localName = "DocumentMainType", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Value mainType;

    @JacksonXmlProperty(localName = "Year", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public IntValue year;

    @JacksonXmlProperty(localName = "Number", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public IntValue number;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AlternativeNumber", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public List<AlternativeNumber> altNumbers;

    public static class AlternativeNumber {

        @JacksonXmlProperty(isAttribute = true)
        public String Category;

        @JacksonXmlProperty(isAttribute = true)
        public String Value;

    }

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public ZonedDateTime updated;

    @JacksonXmlProperty(namespace = "http://www.w3.org/2005/Atom")
    public ZonedDateTime published;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    public List<Link> links;

}

public static class Value {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public String value;

}

public static class IntValue {

    @JacksonXmlProperty(localName = "Value", isAttribute = true)
    public int value;

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
