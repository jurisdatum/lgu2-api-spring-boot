package uk.gov.legislation.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstVersionTest {

    @Test
    void testGetFirstVersionEnacted() {
        assertAll("Enacted types",
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.UKPGA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.UKLA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.UKPPA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.ASP)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.NIA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.AOSP)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.AEP)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.AIP)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.APGB)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.GBLA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.GBPPA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.ANAW)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.ASC)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.MWA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.UKCM)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.MNIA)),
            () -> assertEquals("enacted", FirstVersion.getFirstVersion(Type.APNI))
        );
    }

    @Test
    void testGetFirstVersionMade() {
        assertAll("Made types",
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.UKSI)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.WSI)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.SSI)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.NISI)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.UKMD)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.UKSRO)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.UKDSI)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.NISR)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.NISRO)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.NIDSR)),
            () -> assertEquals("made", FirstVersion.getFirstVersion(Type.SDSI))
        );
    }

    @Test
    void testGetFirstVersionCreated() {
        assertAll("Created types",
            () -> assertEquals("created", FirstVersion.getFirstVersion(Type.UKMO)),
            () -> assertEquals("created", FirstVersion.getFirstVersion(Type.UKCI))
        );
    }

    @Test
    void testGetFirstVersionAdopted() {
        assertAll("Adopted types",
            () -> assertEquals("adopted", FirstVersion.getFirstVersion(Type.EUR)),
            () -> assertEquals("adopted", FirstVersion.getFirstVersion(Type.EUDN)),
            () -> assertEquals("adopted", FirstVersion.getFirstVersion(Type.EUDR)),
            () -> assertEquals("adopted", FirstVersion.getFirstVersion(Type.EUT))
        );
    }

    @Test
    void testGetFirstVersionInvalidType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            FirstVersion.getFirstVersion("INVALID_TYPE")
        );
        assertEquals("INVALID_TYPE", exception.getMessage());
    }
}