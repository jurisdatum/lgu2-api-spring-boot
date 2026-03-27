package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.core.JacksonException;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;
import java.util.List;

@JsonRootName("Contents")
public class Contents {

    public static uk.gov.legislation.transform.simple.Contents parse(String xml) throws JacksonException {
        return SimpleXmlMapper.INSTANCE.readValue(xml, uk.gov.legislation.transform.simple.Contents.class);
    }

    public Metadata meta;

    public Main contents;

    public static class Main {

        public String title;

        @JacksonXmlElementWrapper(localName = "body")
        @JacksonXmlProperty(localName = "item")
        public List<Item> body;

        @JacksonXmlElementWrapper(localName = "appendices")
        @JacksonXmlProperty(localName = "appendix")
        public List<Item> appendices;

        @JacksonXmlElementWrapper(localName = "attachments1")
        @JacksonXmlProperty(localName = "attachment1")
        public List<Item> attachmentsBeforeSchedules;

        @JacksonXmlElementWrapper(localName = "schedules")
        @JacksonXmlProperty(localName = "schedule")
        public List<Item> schedules;

        @JacksonXmlElementWrapper(localName = "attachments")
        @JacksonXmlProperty(localName = "attachment")
        public List<Item> attachments;

    }

    public static class Item {

        public String name;

        public String number;

        public String title;

        @JacksonXmlProperty(localName = "ContentRef", isAttribute = true)
        public String ref;

        @JacksonXmlProperty(localName = "DocumentURI", isAttribute = true)
        public URI uri;

        @JacksonXmlProperty(isAttribute = true)
        public String extent;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        public List<Item> children;

    }

}
