package uk.gov.legislation.data.marklogic.legislation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.data.marklogic.TestMarkLogic;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.exceptions.NoDocumentException;

import java.io.PushbackInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegislationTest {

    private final StubMarkLogic db = new StubMarkLogic();
    private final Legislation legislation = new Legislation(db);

    @Test
    @DisplayName("getDocument treats malformed 404 error payloads as document-not-found, not CLML")
    void getDocumentHandlesMalformed404ErrorPayload() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Document not found</message>
                <unexpected>not allowed</unexpected>
            </error>
            """;
        db.getResponse = xml;

        NoDocumentException ex = assertThrows(NoDocumentException.class,
            () -> legislation.getDocument("ukpga", "2024", 1, Optional.empty(), Optional.empty()));

        assertEquals("Document not found", ex.getMessage());
    }

    @Test
    @DisplayName("getDocument rejects malformed redirect error payloads instead of treating them as CLML")
    void getDocumentRejectsMalformedRedirectPayload() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>307</status-code>
                <message>Redirecting but missing location header</message>
            </error>
            """;
        db.getResponse = xml;

        assertThrows(MarkLogicRequestException.class,
            () -> legislation.getDocument("ukpga", "2024", 1, Optional.empty(), Optional.empty()));
    }

    @Test
    @DisplayName("getDocument rejects malformed error XML instead of treating it as CLML")
    void getDocumentRejectsMalformedErrorXml() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Broken error payload
            """;
        db.getResponse = xml;

        assertThrows(MarkLogicRequestException.class,
            () -> legislation.getDocument("ukpga", "2024", 1, Optional.empty(), Optional.empty()));
    }

    @Test
    @DisplayName("getDocument rejects XML malformed before the root can be classified")
    void getDocumentRejectsMalformedXmlDuringClassification() throws Exception {
        db.getResponse = "<error xmlns=\"\"";

        assertThrows(MarkLogicRequestException.class,
            () -> legislation.getDocument("ukpga", "2024", 1, Optional.empty(), Optional.empty()));
    }

    @Test
    @DisplayName("getDocumentStream treats malformed 404 error payloads as document-not-found, not CLML")
    void getDocumentStreamHandlesMalformed404ErrorPayload() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Document not found</message>
                <unexpected>not allowed</unexpected>
            </error>
            """;
        db.streamResponse = TestMarkLogic.asStream(xml);

        NoDocumentException ex = assertThrows(NoDocumentException.class,
            () -> legislation.getDocumentStream("ukpga", "2024", 1, Optional.empty(), Optional.empty()));

        assertEquals("Document not found", ex.getMessage());
    }

    @Test
    @DisplayName("getDocumentStream rejects malformed error XML instead of treating it as CLML")
    void getDocumentStreamRejectsMalformedErrorXml() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Broken error payload
            """;
        db.streamResponse = TestMarkLogic.asStream(xml);

        assertThrows(MarkLogicRequestException.class,
            () -> legislation.getDocumentStream("ukpga", "2024", 1, Optional.empty(), Optional.empty()));
    }

    @Test
    @DisplayName("getDocumentStream treats an unclassifiable stream as a non-error response (best-effort classification)")
    void getDocumentStreamTreatsUnclassifiableStreamAsNonError() throws Exception {
        db.streamResponse = TestMarkLogic.asStream("<error xmlns=\"\"");

        Legislation.StreamResponse response =
            legislation.getDocumentStream("ukpga", "2024", 1, Optional.empty(), Optional.empty());
        assertEquals(Optional.empty(), response.redirect());
    }

    @Test
    @DisplayName("getDocumentStream rejects malformed redirect error payloads instead of treating them as CLML")
    void getDocumentStreamRejectsMalformedRedirectPayload() throws Exception {
        String xml = """
            <error xmlns="">
                <status-code>307</status-code>
                <message>Redirecting but missing location header</message>
            </error>
            """;
        db.streamResponse = TestMarkLogic.asStream(xml);

        assertThrows(MarkLogicRequestException.class,
            () -> legislation.getDocumentStream("ukpga", "2024", 1, Optional.empty(), Optional.empty()));
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
