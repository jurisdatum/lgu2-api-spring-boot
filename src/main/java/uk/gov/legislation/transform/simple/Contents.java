package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement()
public class Contents {

    public static uk.gov.legislation.transform.simple.Contents parse(String xml) throws JsonProcessingException {
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

        public String ref;

        @JacksonXmlProperty(isAttribute = true)
        public String extent;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        public List<Item> children;

    }

}
