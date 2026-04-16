package uk.gov.legislation.data.marklogic.legislationbyid;

import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.MarkLogic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LegislationByIdTest {

    private final MarkLogic db = mock(MarkLogic.class);
    private final LegislationById legislationById = new LegislationById(db);

    @Test
    void exists_returnsTrue_whenMarkLogicReturns303() throws Exception {
        String response = """
                <error xmlns="">
                    <status-code>303</status-code>
                    <header>
                        <name>Location</name>
                        <value>http://www.legislation.gov.uk/ukpga/2024/1/section/1</value>
                    </header>
                </error>
                """;
        when(db.get(eq("legislation-by-id.xq"), anyString())).thenReturn(response);

        assertTrue(legislationById.exists("ukpga", "2024", 1, "section-1-1"));
    }

    @Test
    void exists_returnsFalse_whenMarkLogicReturns404() throws Exception {
        String response = """
                <error xmlns="">
                    <status-code>404</status-code>
                    <message>We couldn't find exactly what you're looking for.</message>
                    <link href="http://www.legislation.gov.uk/ukpga/2024/1">Post Office Act 2024</link>
                </error>
                """;
        when(db.get(eq("legislation-by-id.xq"), anyString())).thenReturn(response);

        assertFalse(legislationById.exists("ukpga", "2024", 1, "section-1-7"));
    }

    @Test
    void exists_buildsCorrectQueryString() throws Exception {
        String response = """
                <error xmlns="">
                    <status-code>303</status-code>
                    <header>
                        <name>Location</name>
                        <value>http://www.legislation.gov.uk/ukpga/2024/1/section/1</value>
                    </header>
                </error>
                """;
        when(db.get(eq("legislation-by-id.xq"), anyString())).thenReturn(response);

        legislationById.exists("ukpga", "2024", 1, "section-1-1");

        verify(db).get("legislation-by-id.xq", "?type=ukpga&year=2024&number=1&section=section-1-1");
    }

    @Test
    void exists_encodesRegnalYear() throws Exception {
        String response = """
                <error xmlns="">
                    <status-code>303</status-code>
                    <header>
                        <name>Location</name>
                        <value>http://www.legislation.gov.uk/aep/Edw1/25/9/section/1</value>
                    </header>
                </error>
                """;
        when(db.get(eq("legislation-by-id.xq"), anyString())).thenReturn(response);

        legislationById.exists("aep", "Edw1/25", 9, "section-1");

        // The '/' in the regnal year "Edw1/25" must be URL-encoded as "%2F"
        verify(db).get("legislation-by-id.xq", "?type=aep&year=Edw1%2F25&number=9&section=section-1");
    }

}

