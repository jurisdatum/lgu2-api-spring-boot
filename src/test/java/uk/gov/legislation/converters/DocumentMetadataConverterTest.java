package uk.gov.legislation.converters;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.CommonMetadata;
import uk.gov.legislation.transform.simple.Metadata;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentMetadataConverterTest {

    @Test
    void revisedWholeDocument_withoutPointInTime_computesUpToDate() {
        Metadata simple = new Metadata();
        simple.longType = "UnitedKingdomPublicGeneralAct";
        simple.status = Metadata.REVISED;
        simple.setValid("2024-11-22");
        simple.setVersions(List.of(Metadata.HasVersionEntry.of(null, "current")));

        assertTrue(DocumentMetadataConverter.shouldComputeUpToDate(simple));
    }

    @Test
    void revisedFragment_latestPayloadAfterLastFragmentMilestone_computesUpToDate() {
        Metadata simple = new Metadata();
        simple.longType = "ScottishOldAct";
        simple.status = Metadata.REVISED;
        simple.setValid("2007-01-01");
        simple.setVersions(List.of(Metadata.HasVersionEntry.of(null, "1991-02-01")));

        assertTrue(DocumentMetadataConverter.shouldComputeUpToDate(simple));
    }

    @Test
    void revisedFragment_priorMilestone_doesNotComputeUpToDate() {
        Metadata simple = new Metadata();
        simple.longType = "ScottishOldAct";
        simple.status = Metadata.REVISED;
        simple.setValid("2000-01-01");
        simple.setVersions(List.of(
            Metadata.HasVersionEntry.of(null, "1991-02-01"),
            Metadata.HasVersionEntry.of(null, "2007-01-01")));

        assertFalse(DocumentMetadataConverter.shouldComputeUpToDate(simple));
    }

    @Test
    void testConvertAltNumbersWithEmptyList() {
        List <CommonMetadata.AltNumber> result = DocumentMetadataConverter.convertAltNumbers(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertAltNumbersWithValidAltNumbers() {
        Metadata.AltNum simpleAltNum1 = createAltNum("C", "1");
        Metadata.AltNum simpleAltNum2 = createAltNum("L", "2");

        List <CommonMetadata.AltNumber> result = DocumentMetadataConverter.convertAltNumbers(List.of(simpleAltNum1, simpleAltNum2));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("C", result.get(0).category);
        assertEquals("1", result.get(0).value);
        assertEquals("L", result.get(1).category);
        assertEquals("2", result.get(1).value);
    }

    private Metadata.AltNum createAltNum(String category, String value) {
        Metadata.AltNum altNum = new Metadata.AltNum();
        altNum.category = category;
        altNum.value = value;
        return altNum;
    }
}
