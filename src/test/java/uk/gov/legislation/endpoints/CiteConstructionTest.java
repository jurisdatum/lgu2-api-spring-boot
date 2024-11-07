package uk.gov.legislation.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.util.Cites;

import java.util.Collection;
import java.util.Collections;

public class CiteConstructionTest {

    @Test
    public void ukla() {
        String actual = Cites.make("ukla", 2024, 1, null);
        String expected = "2024 c. i";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void asp() {
        String actual = Cites.make("asp", 2024, 13, null);
        String expected = "2024 asp 13";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void nia() {
        String actual = Cites.make("nia", 2024, 1, null);
        String expected = "2024 c. 1";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void aosp() {
        String actual = Cites.make("aosp", 1707, 8, null);
        String expected = "1707 c. 8";
        Assertions.assertEquals(expected, actual);
    }

    private record AltNumber(String category, String value) implements uk.gov.legislation.util.AltNumber { }

    @Test
    public void aep() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "6_Ann"));
        String actual = Cites.make("aep", 1706, 11, altNumbers);
        String expected = "1706 c. 11 (Regnal. 6_Ann)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void aip() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "40_Geo_3"));
        String actual = Cites.make("aip", 1800, 38, altNumbers);
        String expected = "1800 c. 38 (Regnal. 40_Geo_3)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void apgb() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "39_and_40_Geo_3"));
        String actual = Cites.make("apgb", 1800, 88, altNumbers);
        String expected = "1800 c. 88 (Regnal. 39_and_40_Geo_3)";
        Assertions.assertEquals(expected, actual);
    }

//    @Test
//    public void gbla() {
//        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "39_and_40_Geo_3"));
//        String actual = Cites.make("gbla", 1800, 120, altNumbers);
//        String expected = "1800 c. cxx (Regnal. 39_and_40_Geo_3)";
//        Assertions.assertEquals(expected, actual);
//    }

    @Test
    public void anaw() {
        String actual = Cites.make("anaw", 2020, 3, null);
        String expected = "2020 anaw 3";
        Assertions.assertEquals(expected, actual);
    }

//    @Test
//    public void asc() {
//        String actual = Cites.make("asc", 2024, 6, null);
//        String expected = "2024 asc 6";
//        Assertions.assertEquals(expected, actual);
//    }

    @Test
    public void mwa() {
        String actual = Cites.make("mwa", 2011, 7, null);
        String expected = "2011 nawm 7";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void ukcm() {
        String actual = Cites.make("ukcm", 2024, 2, null);
        String expected = "2024 No. 2";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void mnia() {
        String actual = Cites.make("mnia", 1974, 4, null);
        String expected = "1974 Chapter 4";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void apni() {
        String actual = Cites.make("apni", 1972, 15, null);
        String expected = "1972 Chapter 15";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void wsi() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("W", "169"));
        String actual = Cites.make("wsi", 2024, 998, altNumbers);
        String expected = "2024 No. 998 (W. 169)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void nisi() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("NI", "1"));
        String actual = Cites.make("nisi", 2016, 999, altNumbers);
        String expected = "2016 No. 999 (N.I. 1)";
        Assertions.assertEquals(expected, actual);
    }

}
