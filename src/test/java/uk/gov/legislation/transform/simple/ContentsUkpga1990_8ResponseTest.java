package uk.gov.legislation.transform.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ContentsUkpga1990_8ResponseTest {

    @Test
    void contentsJsonMatchesFixture() throws Exception {
        String xml = UnappliedEffectsHelper.read("/ukpga_1990_8_contents.xml");
        String expected = UnappliedEffectsHelper.read("/ukpga_1990_8_contents.json");

        Simplify simplify = new Simplify();
        Contents contents = simplify.contents(xml);
        String actual = UnappliedEffectsTest.mapper.writeValueAsString(contents);

        assertEquals(expected, actual);
    }
}
