package uk.gov.legislation.data.marklogic.notes;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import uk.gov.legislation.data.marklogic.models.AtomLink;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.time.LocalDate;
import java.util.List;

public class EN {
    @JacksonXmlProperty(localName = "Metadata", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    public Metadata metadata;

    public  static class Metadata {
        @JacksonXmlProperty(localName = "identifier",
            namespace = "http://purl.org/dc/elements/1.1/"
        )
        public String identifier;

        @JacksonXmlProperty(
            localName = "title",
            namespace = "http://purl.org/dc/elements/1.1/"
        )
        public String title;

        @JacksonXmlProperty(
            localName = "publisher",
            namespace = "http://purl.org/dc/elements/1.1/"
        )
        public String publisher;

        @JacksonXmlProperty(
            localName = "modified",
            namespace = "http://purl.org/dc/elements/1.1/"
        )
        public LocalDate modified;

        @JacksonXmlProperty(localName = "lang")
        public String lang;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "link", namespace = "http://www.w3.org/2005/Atom")
        public List <AtomLink> links;

        @JacksonXmlProperty(
            localName = "ENmetadata",
            namespace = "http://www.legislation.gov.uk/namespaces/metadata"
        )
        public ENMetadata enMetadata;

    }

    public static class ENMetadata {

        @JacksonXmlProperty(
            localName = "DocumentClassification",
            namespace = "http://www.legislation.gov.uk/namespaces/metadata"
        )
        public DocumentClassification documentClassification;

        @JacksonXmlProperty(localName = "Year")
        public SearchResults.IntValue year;

        @JacksonXmlProperty(localName = "Number")
        public SearchResults.IntValue number;
    }


    public static class DocumentClassification {

        @JacksonXmlProperty(
            localName = "DocumentCategory",
            namespace = "http://www.legislation.gov.uk/namespaces/metadata"
        )
        public SearchResults.Value documentCategory;

        @JacksonXmlProperty(
            localName = "DocumentMainType",
            namespace = "http://www.legislation.gov.uk/namespaces/metadata"
        )
        public SearchResults.Value documentMainType;

        @JacksonXmlProperty(
            localName = "DocumentStatus",
            namespace = "http://www.legislation.gov.uk/namespaces/metadata"
        )
        public SearchResults.Value documentStatus;
    }


}
