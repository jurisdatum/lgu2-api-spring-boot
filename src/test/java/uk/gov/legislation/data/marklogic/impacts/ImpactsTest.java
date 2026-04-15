package uk.gov.legislation.data.marklogic.impacts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.TestMarkLogic;
import uk.gov.legislation.exceptions.MarkLogicRequestException;

import java.io.PushbackInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImpactsTest {

    private final StubMarkLogic db = new StubMarkLogic();
    private final Impacts impacts = new Impacts(db);

    @Test
    @DisplayName("getXml treats error-root responses as empty")
    void getXmlTreatsErrorRootAsEmpty() throws Exception {
        db.getResponse = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Impact assessment not found</message>
            </error>
            """;

        assertEquals(Optional.empty(), impacts.getXml(2024, 1));
    }

    @Test
    @DisplayName("getXml throws MarkLogicRequestException for malformed XML")
    void getXmlThrowsForMalformedXml() {
        db.getResponse = "<error xmlns=\"\"";

        assertThrows(MarkLogicRequestException.class, () -> impacts.getXml(2024, 1));
    }

    @Test
    @DisplayName("getStream treats an unclassifiable stream as a non-error response (best-effort classification)")
    void getStreamTreatsUnclassifiableStreamAsNonError() throws Exception {
        db.streamResponse = TestMarkLogic.asStream("<error xmlns=\"\"");

        assertTrue(impacts.getStream(2024, 1).isPresent());
    }

    private static final class StubMarkLogic extends TestMarkLogic {

        private String getResponse;
        private PushbackInputStream streamResponse;

        @Override
        public String get(String endpoint, String query) {
            return getResponse;
        }

        @Override
        public PushbackInputStream getStream(String endpoint, String query) {
            return streamResponse;
        }

    }

}
