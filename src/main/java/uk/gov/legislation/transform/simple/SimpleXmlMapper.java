package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Central place to build the XML-aware Jackson mapper used across the
 * simplification pipeline. The mapper is immutable and thread-safe, so we
 * expose a single shared instance to avoid repeated configuration work in hot
 * paths.
 */
public final class SimpleXmlMapper {

    private SimpleXmlMapper() {
        // no instances
    }

    public static final XmlMapper INSTANCE = (XmlMapper) new XmlMapper()
        .registerModules(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

}
