package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.util.Links;

import static org.junit.jupiter.api.Assertions.*;

class UriParsingTest {

    @Test
    void http_lgu_ukpga_2024_1() {
        String uri = "http://www.legislation.gov.uk/ukpga/2024/1";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
    }

    @Test
    void http_lgu_ukpga_2024_1_() {
        String uri = "http://www.legislation.gov.uk/ukpga/2024/1/";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
        assertTrue(comp.language().isEmpty());
    }

    @Test
    void https_lgu_ukpga_2024_1() {
        String uri = "https://www.legislation.gov.uk/ukpga/2024/1";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
    }

    @Test
    void http_lgu_id_ukpga_2024_1() {
        String uri = "http://www.legislation.gov.uk/id/ukpga/2024/1";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
    }

    /* versions */

    @Test
    void https_lgu_ukpga_2024_1_enacted_() {
        String uri = "https://www.legislation.gov.uk/ukpga/2024/1/enacted/";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("enacted", comp.version().get());
    }

    @Test
    void http_lgu_ukpga_2024_1_revision() {
        String uri = "http://www.legislation.gov.uk/ukpga/2024/1/revision";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
        assertTrue(comp.language().isEmpty());
    }

    @Test
    void http_lgu_ukpga_2024_1_enacted_revision() {
        String uri = "http://www.legislation.gov.uk/ukpga/2024/1/enacted/revision";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("enacted", comp.version().get());
    }

    @Test
    void http_lgu_id_ukpga_2024_1_2025_01_01_() {
        String uri = "http://www.legislation.gov.uk/id/ukpga/2024/1/2025-01-01/";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("2025-01-01", comp.version().get());
    }

    @Test
    void http_lgu_uksi_2024_1_made() {
        String uri = "http://www.legislation.gov.uk/uksi/2024/1/made";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("uksi", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("made", comp.version().get());
    }

    /* regnal years */

    @Test
    void http_lgu_ukpga_Geo5_1_2_20_enacted() {
        String uri = "http://www.legislation.gov.uk/ukpga/Geo5/1-2/20/enacted";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("Geo5/1-2", comp.year());
        assertEquals(20, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("enacted", comp.version().get());
    }

    /* fragments */

    @Test
    void http_lgu_ukpga_2024_1_section_1() {
        String uri = "http://www.legislation.gov.uk/ukpga/2024/1/section/1";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1, comp.number());
        assertTrue(comp.fragment().isPresent());
        assertEquals("section/1", comp.fragment().get());
        assertTrue(comp.version().isEmpty());
        assertTrue(comp.language().isEmpty());
    }

    @Test
    void https_lgu_ukpga_2024_25_schedule_paragraph_1_enacted() {
        String uri = "https://www.legislation.gov.uk/ukpga/2024/25/schedule/paragraph/1/enacted";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(25, comp.number());
        assertTrue(comp.fragment().isPresent());
        assertEquals("schedule/paragraph/1", comp.fragment().get());
        assertTrue(comp.version().isPresent());
        assertEquals("enacted", comp.version().get());
    }

    /* tables of contents */

    @Test
    void https_lgu_id_ukpga_2024_25_contents_enacted() {
        String uri = "https://www.legislation.gov.uk/id/ukpga/2024/25/contents/enacted";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(25, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("enacted", comp.version().get());
    }

    @Test
    void https_lgu_ukpga_2024_25_contents_() {
        String uri = "https://www.legislation.gov.uk/ukpga/2024/25/contents/";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("ukpga", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(25, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
    }

    /* language */

    @Test
    void http_lgu_wsi_2024_1002_welsh() {
        String uri = "http://www.legislation.gov.uk/wsi/2024/1002/welsh";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("wsi", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1002, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isEmpty());
        assertTrue(comp.language().isPresent());
        assertEquals("welsh", comp.language().get());
    }

    @Test
    void http_lgu_wsi_2024_1002_made_welsh() {
        String uri = "http://www.legislation.gov.uk/wsi/2024/1002/made/welsh";
        Links.Components comp = Links.parse(uri);
        assertNotNull(comp);
        assertEquals("wsi", comp.type());
        assertEquals("2024", comp.year());
        assertEquals(1002, comp.number());
        assertTrue(comp.fragment().isEmpty());
        assertTrue(comp.version().isPresent());
        assertEquals("made", comp.version().get());
        assertTrue(comp.language().isPresent());
        assertEquals("welsh", comp.language().get());
    }

    /* other */

    @Test
    void google() {
        String uri = "http://www.google.com/";
        Links.Components comp = Links.parse(uri);
        assertNull(comp);
    }

}
