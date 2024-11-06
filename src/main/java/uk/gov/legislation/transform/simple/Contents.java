package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.gov.legislation.endpoints.document.TableOfContents;

import java.util.List;

@JacksonXmlRootElement()
public class Contents implements TableOfContents {

    private static XmlMapper mapper() {
        return (XmlMapper) new XmlMapper().registerModules(new JavaTimeModule());
    }
    public static uk.gov.legislation.transform.simple.Contents parse(String xml) throws JsonProcessingException {
        return mapper().readValue(xml, uk.gov.legislation.transform.simple.Contents.class);
    }

    public Metadata meta;

    public Metadata meta() {
        return meta;
    }

    public Main contents;

    public Main contents() {
        return contents;
    }

    public static class Main implements TableOfContents.Contents {

        public String title;

        public String title() { return title; }

        @JacksonXmlElementWrapper(localName = "body")
        @JacksonXmlProperty(localName = "item")
        public List<Item> body;

        public List<Item> body() { return body; }

        @JacksonXmlElementWrapper(localName = "appendices")
        @JacksonXmlProperty(localName = "appendix")
        public List<Item> appendices;

        public List<Item> appendices() { return appendices; }

        @JacksonXmlElementWrapper(localName = "attachments1")
        @JacksonXmlProperty(localName = "attachment1")
        public List<Item> attachmentsBeforeSchedules;

        public List<Item> attachmentsBeforeSchedules() { return attachmentsBeforeSchedules; }

        @JacksonXmlElementWrapper(localName = "schedules")
        @JacksonXmlProperty(localName = "schedule")
        public List<Item> schedules;

        public List<Item> schedules() { return schedules; }

        @JacksonXmlElementWrapper(localName = "attachments")
        @JacksonXmlProperty(localName = "attachment")
        public List<Item> attachments;

        public List<Item> attachments() { return attachments; }

    }

    public static class Item implements TableOfContents.Item {

        public String name;

        public String name() { return name; }

        public String number;

        public String number() { return number; }

        public String title;

        public String title() { return title; }

        public String ref;

        public String ref() { return ref; }

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        public List<Item> children;

        public List<Item> children() { return children; }

    }

}
