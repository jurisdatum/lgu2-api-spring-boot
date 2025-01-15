package uk.gov.legislation.transform.simple.effects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@JacksonXmlRootElement(localName = "feed", namespace = "http://www.w3.org/2005/Atom")
public class Page {

    @JacksonXmlProperty
    public String id;

    @JacksonXmlProperty
    public ZonedDateTime updated;

    @JacksonXmlProperty(namespace = "http://a9.com/-/spec/opensearch/1.1/")
    public int itemsPerPage;

    @JacksonXmlProperty(namespace = "http://a9.com/-/spec/opensearch/1.1/")
    public int startIndex;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public int page;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public int morePages;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
    public int totalPages;

    @JacksonXmlProperty(namespace = "http://a9.com/-/spec/opensearch/1.1/")
    public int totalResults;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "entry")
    public List<Entry> entries = Collections.emptyList();

}
