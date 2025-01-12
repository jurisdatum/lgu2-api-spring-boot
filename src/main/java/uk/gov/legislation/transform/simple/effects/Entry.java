package uk.gov.legislation.transform.simple.effects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.ZonedDateTime;

public class Entry {

    @JacksonXmlProperty
    public String id;

    @JacksonXmlProperty
    public Content content;

    @JacksonXmlProperty
    public String title;

    @JacksonXmlProperty
    public ZonedDateTime updated;

    @JacksonXmlProperty
    public Author author;


    public static class Content {

        @JacksonXmlProperty(localName = "Effect", namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        public Effect effect;

    }

    public static class Author {

        @JacksonXmlProperty
        public String name;

    }

}
