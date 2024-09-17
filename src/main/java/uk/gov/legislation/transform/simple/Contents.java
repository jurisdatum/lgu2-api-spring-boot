package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

@JacksonXmlRootElement()
public class Contents {

    private static XmlMapper mapper() {
        return (XmlMapper) new XmlMapper().registerModules(new JavaTimeModule());
    }
    public static uk.gov.legislation.transform.simple.Contents parse(String clml) throws JsonProcessingException {
        return mapper().readValue(clml, uk.gov.legislation.transform.simple.Contents.class);
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
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> appendices;

        @JacksonXmlElementWrapper(localName = "attachments1")
        @JacksonXmlProperty(localName = "attachment1")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> attachmentsBeforeSchedules;

        @JacksonXmlElementWrapper(localName = "schedules")
        @JacksonXmlProperty(localName = "schedule")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> schedules;

        @JacksonXmlElementWrapper(localName = "attachments")
        @JacksonXmlProperty(localName = "attachment")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> attachments;

    }

    public static class Item {

        public String name;

        public String number;

        public String title;

        public String ref;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<Item> children;

    }

}
