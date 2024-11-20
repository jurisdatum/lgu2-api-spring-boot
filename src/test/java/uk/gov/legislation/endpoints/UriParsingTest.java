package uk.gov.legislation.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.util.Links;

class UriParsingTest {

    @Test
    void regnalYear() {
        String uri = "http://www.legislation.gov.uk/ukpga/Geo5/1-2/20/enacted";
        String year = "Geo5/1-2";
        Links.Components comp = Links.parse(uri);
        Assertions.assertEquals(year, comp.year());
    }

}
