package uk.gov.legislation.data.marklogic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorParsingTest {

    @Test
    @DisplayName("Error.parse succeeds on MarkLogic <error> payloads")
    void parseValidErrorXml() {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>The item of legislation you've requested isn't available on this site and we can't find any references to it. Perhaps the link is slightly wrong.</message>
                <link href="http://www.legislation.gov.uk/uksi/2025">Browse for other legislation of this type from 2025</link><link href="http://www.legislation.gov.uk/uksi">Browse for other legislation of this type</link>
            </error>
            """;

        assertDoesNotThrow(() -> Error.parse(xml));
    }

    @Test
    @DisplayName("Error.parse rejects CLML responses")
    void parseRejectsClmlXml() throws IOException {
        ClassPathResource resource = new ClassPathResource("ukpga_2023_29/ukpga-2023-29-2024-11-01.xml");
        String xml = resource.getContentAsString(StandardCharsets.UTF_8);

        assertThrows(JacksonException.class, () -> Error.parse(xml));
    }

    @Test
    @DisplayName("Error.parse rejects MarkLogic errors missing status code")
    void parseRejectsMissingStatusXml() {
        String xml = """
            <error xmlns="">
                <message>Missing status code</message>
            </error>
            """;

        assertThrows(IllegalArgumentException.class, () -> Error.parse(xml));
    }

    @Test
    @DisplayName("Error.parse succeeds on MarkLogic redirect errors")
    void parseValidRedirectErrorXml() {
        String xml = """
            <error xmlns="">
                <status-code>307</status-code>
                <message>Redirecting you to The East Yorkshire Solar Farm (Correction) Order 2025 as made</message>
                <header>
                    <name>Location</name>
                    <value>http://www.legislation.gov.uk/uksi/2025/1059/made</value>
                </header>
            </error>
            """;

        assertDoesNotThrow(() -> Error.parse(xml));
    }

}
