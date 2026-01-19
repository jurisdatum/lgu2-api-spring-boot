package uk.gov.legislation.data.marklogic.impacts;

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import uk.gov.legislation.data.marklogic.models.AtomLink;
import uk.gov.legislation.data.marklogic.models.LocalDateValue;
import uk.gov.legislation.data.marklogic.search.SearchResults;
import uk.gov.legislation.transform.simple.Alternative;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@JacksonXmlRootElement(namespace = "http://www.legislation.gov.uk/namespaces/legislation")
public class ImpactAssessment {

    @JacksonXmlProperty(localName = "Metadata", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Metadata metadata;

    public static class Metadata {

        @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
        public String identifier;

        @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
        public String title;

        @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
        public String publisher;

        @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
        public LocalDate modified;

        @JacksonXmlProperty(namespace = "http://purl.org/dc/terms/")
        public LocalDate valid;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "link", namespace = "http://www.w3.org/2005/Atom")
        public List<AtomLink> links;

        @JacksonXmlProperty(localName = "ImpactAssessmentMetadata", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public ImpactAssessmentMetadata impactAssessmentMetadata;

        @JacksonXmlElementWrapper(localName = "Alternatives", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @JacksonXmlProperty(localName = "Alternative", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public List<Alternative> alternatives;

        @JacksonXmlProperty(localName = "Legislation", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public Legislation legislation;

    }

    public static class ImpactAssessmentMetadata {

        @JacksonXmlProperty(localName = "DocumentClassification", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public DocumentClassification documentClassification;

        @JacksonXmlProperty(localName = "Year", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public SearchResults.IntValue year;

        @JacksonXmlProperty(localName = "Number", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public SearchResults.IntValue number;

        @JacksonXmlProperty(localName = "Date", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public LocalDateValue date;

        @JacksonXmlProperty(localName = "Department", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public SearchResults.Value department;

    }

    public static class DocumentClassification {

        @JacksonXmlProperty(localName = "DocumentMainType", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public SearchResults.Value documentMainType;

        @JacksonXmlProperty(localName = "DocumentStage", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public SearchResults.Value documentStage;

    }

    public static class Legislation {

        @JacksonXmlProperty(localName = "URI", isAttribute = true)
        public URI uri;

        @JacksonXmlProperty(localName = "Class", isAttribute = true)
        public String clazz;

        @JacksonXmlProperty(localName = "Year", isAttribute = true)
        public int year;

        @JacksonXmlProperty(localName = "Number", isAttribute = true)
        public long number;

    }

}
