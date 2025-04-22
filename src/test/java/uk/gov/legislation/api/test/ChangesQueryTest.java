package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.changes.Parameters;

class ChangesQueryTest {

    @Test
    void params() {
        String actual = Parameters.builder().affectedType("ukpga").affectedYear(1968).build().toQuery();
        String expected = "?affected-type=ukpga&affected-year=1968";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void search() {
        String actual = uk.gov.legislation.data.marklogic.search.Parameters.builder()
            .type("ukpga").year(1968).build().toQuery();
        String expected = "?type=ukpga&year=1968";
        Assertions.assertEquals(expected, actual);
    }

}
