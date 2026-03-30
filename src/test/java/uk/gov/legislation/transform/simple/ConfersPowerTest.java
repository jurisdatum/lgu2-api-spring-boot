package uk.gov.legislation.transform.simple;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.converters.FragmentMetadataConverter;
import uk.gov.legislation.transform.TransformHelper;

import static org.junit.jupiter.api.Assertions.*;

class ConfersPowerTest {

    @Test
    void fragmentMetadataIncludesConfersPowerFromP1group() throws Exception {
        String clml = UnappliedEffectsHelper.read("/ukpga_2026_2/ukpga-2026-2-section-22.xml");

        Simplify simplify = new Simplify();
        Metadata simple = simplify.extractFragmentMetadata(clml);
        FragmentMetadata meta = FragmentMetadataConverter.convert(simple);

        assertNotNull(meta.fragmentInfo);
        assertEquals("section-22", meta.fragmentInfo.id);
        assertEquals(Boolean.TRUE, meta.fragmentInfo.confersPower);

        String fragmentInfoJson = TransformHelper.MAPPER.writeValueAsString(meta.fragmentInfo);
        assertTrue(fragmentInfoJson.contains("\"confersPower\" : true"));

        var subSection = meta.descendants.stream()
            .filter(level -> "section-22-1".equals(level.id))
            .findFirst()
            .orElseThrow();
        assertEquals(Boolean.FALSE, subSection.confersPower);

        String subSectionJson = TransformHelper.MAPPER.writeValueAsString(subSection);
        assertFalse(subSectionJson.contains("\"confersPower\""));
    }

}
