package uk.gov.legislation.transform.simple;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;

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

    public static final XmlMapper INSTANCE = XmlMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

}
