package uk.gov.legislation.data.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

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

        assertThrows(JsonProcessingException.class, () -> Error.parse(xml));
    }

}
