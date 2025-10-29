package uk.gov.legislation.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

class CiteConstructionTest {

    @Test
    void ukla() {
        String actual = Cites.make("ukla", 2024, 1, null);
        String expected = "2024 c. i";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void uksi() {
        String actual = Cites.make("uksi", 2024, 1, null);
        String expected = "S.I. 2024/1";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void asp() {
        String actual = Cites.make("asp", 2024, 13, null);
        String expected = "2024 asp 13";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nia() {
        String actual = Cites.make("nia", 2024, 1, null);
        String expected = "2024 c. 1 (N.I.)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void aosp() {
        String actual = Cites.make("aosp", 1707, 8, null);
        String expected = "1707 c. 8 [S]";
        Assertions.assertEquals(expected, actual);
    }

    private record AltNumber(String category, String value) implements uk.gov.legislation.util.AltNumber { }

    @Test
    void aep() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "6_Ann"));
        String actual = Cites.make("aep", 1706, 11, altNumbers);
        String expected = "1706 (6 Ann.) c. 11";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void aip() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "40_Geo_3"));
        String actual = Cites.make("aip", 1800, 38, altNumbers);
        String expected = "1800 (40 Geo. 3) c. 38 [I]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void apgb() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "39_and_40_Geo_3"));
        String actual = Cites.make("apgb", 1800, 88, altNumbers);
        String expected = "1800 (39 & 40 Geo. 3) c. 88";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void gbla() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("Regnal", "39_and_40_Geo_3"));
        String actual = Cites.make("gbla", 1800, 120, altNumbers);
        String expected = "1800 (39 & 40 Geo. 3) c. cxx";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void anaw() {
        String actual = Cites.make("anaw", 2020, 3, null);
        String expected = "2020 anaw 3";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void asc() {
        String actual = Cites.make("asc", 2024, 6, null);
        String expected = "2024 asc 6";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void mwa() {
        String actual = Cites.make("mwa", 2011, 7, null);
        String expected = "2011 nawm 7";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void ukcm() {
        String actual = Cites.make("ukcm", 2024, 2, null);
        String expected = "2024 No. 2";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void mnia() {
        String actual = Cites.make("mnia", 1974, 4, null);
        String expected = "1974 c. 4 (N.I.)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void apni() {
        String actual = Cites.make("apni", 1972, 15, null);
        String expected = "1972 c. 15 (N.I.)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void ssi() {
        String actual = Cites.make("ssi", 2024, 193, null);
        String expected = "S.S.I. 2024/193";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void wsi() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("W", "169"));
        String actual = Cites.make("wsi", 2024, 998, altNumbers);
        String expected = "S.I. 2024/998 (W. 169)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nisi() {
        Collection<AltNumber> altNumbers = Collections.singletonList(new AltNumber("NI", "1"));
        String actual = Cites.make("nisi", 2016, 999, altNumbers);
        String expected = "S.I. 2016/999 (N.I. 1)";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void ukmo() {
        String actual = Cites.make("ukmo", 2020, 12, null);
        String expected = "Ministerial Order 2020/12";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void nisr() {
        String actual = Cites.make("nisr", 2024, 193, null);
        String expected = "S.R. 2024/193";
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void ukci() {
        String actual = Cites.make("ukci", 2024, 1, null);
        String expected = "Church Instrument  2024/1";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void uksro() {
        String actual = Cites.make("uksro", 1923, 405, null);
        String expected = "S.R. & O. 1923/405";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void ukpga() {
        String actual = Cites.make("ukpga", 2024, 1, null);
        String expected = "2024 c. 1";
        Assertions.assertEquals(expected, actual);
    }

}
