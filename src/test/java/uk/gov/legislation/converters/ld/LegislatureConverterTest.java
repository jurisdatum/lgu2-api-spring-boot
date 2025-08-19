package uk.gov.legislation.converters.ld;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.api.responses.ld.Legislature;
import uk.gov.legislation.data.virtuoso.jsonld.LegislatureLD;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LegislatureConverterTest {

    @Test
    void testConvert_ShouldMapUriCorrectly() {
        LegislatureLD mockLegislatureLD = mock(LegislatureLD.class);
        URI mockUri = URI.create("http://example.com/legislature/1");
        mockLegislatureLD.id = mockUri;

        Legislature result = LegislatureConverter.convert(mockLegislatureLD);

        assertEquals(mockUri, result.uri, "The URI in the converted Legislature object should match the URI in the LegislatureLD object");
    }
}