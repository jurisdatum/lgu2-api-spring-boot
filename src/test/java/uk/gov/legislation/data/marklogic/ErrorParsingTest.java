package uk.gov.legislation.data.marklogic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorParsingTest {

    @Test
    @DisplayName("Error.classifyRoot recognizes MarkLogic error payloads")
    void classifyRootRecognizesErrorXml() {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Document not found</message>
            </error>
            """;

        assertEquals(Error.RootClassification.ERROR, Error.classifyRoot(xml));
    }

    @Test
    @DisplayName("Error.classifyRoot distinguishes CLML from error payloads")
    void classifyRootRecognizesNonErrorXml() throws IOException {
        ClassPathResource resource = new ClassPathResource("ukpga_2023_29/ukpga-2023-29-2024-11-01.xml");
        String xml = resource.getContentAsString(StandardCharsets.UTF_8);

        assertEquals(Error.RootClassification.OTHER, Error.classifyRoot(xml));
    }

    @Test
    @DisplayName("Error.classifyRoot returns malformed for malformed XML")
    void classifyRootRejectsMalformedXml() {
        assertEquals(Error.RootClassification.MALFORMED, Error.classifyRoot("<error xmlns=\"\""));
    }

    @Test
    @DisplayName("Error.classifyRoot accepts XML with a leading UTF-8 BOM")
    void classifyRootAcceptsBomPrefixedXml() {
        String xml = "\uFEFF<error xmlns=\"\"><status-code>404</status-code><message>m</message></error>";

        assertEquals(Error.RootClassification.ERROR, Error.classifyRoot(xml));
    }

    @Test
    @DisplayName("Error.parse accepts XML with a leading UTF-8 BOM")
    void parseAcceptsBomPrefixedXml() throws JacksonException {
        String xml = "\uFEFF<error xmlns=\"\"><status-code>404</status-code><message>m</message></error>";

        Error error = Error.parse(xml);

        assertEquals(404, error.statusCode);
        assertEquals("m", error.message);
    }

    @Test
    @DisplayName("Error.classifyRoot(stream) is best-effort: returns OTHER when it cannot classify the sample")
    void classifyRootStreamIsBestEffort() throws IOException {
        try (PushbackInputStream input = TestMarkLogic.asStream("<error xmlns=\"\"")) {
            assertEquals(Error.RootClassification.OTHER, Error.classifyRoot(input));
        }
    }

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
    @DisplayName("Error.parse succeeds on whitespace text nodes between repeated links")
    void parseValidErrorXmlWithWhitespaceBetweenLinks() {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>The item of legislation you've requested isn't available on this site and we can't find any references to it. Perhaps the link is slightly wrong.</message>
                <link href="http://www.legislation.gov.uk/uksi/2025">Browse for other legislation of this type from 2025</link>

                <link href="http://www.legislation.gov.uk/uksi">Browse for other legislation of this type</link>
            </error>
            """;

        assertDoesNotThrow(() -> Error.parse(xml));
    }

    @Test
    @DisplayName("Error.parse returns an Error object for malformed error payloads")
    void parseMalformedErrorWithUnexpectedChild() throws JacksonException {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>Unexpected child element</message>
                <unexpected>not allowed</unexpected>
            </error>
            """;

        Error error = Error.parse(xml);

        assertEquals(404, error.statusCode);
        assertEquals("Unexpected child element", error.message);
    }

    @Test
    @DisplayName("Error.parse returns an Error object for malformed redirect payloads")
    void parseMalformedRedirectError() throws JacksonException {
        String xml = """
            <error xmlns="">
                <status-code>307</status-code>
                <message>Redirecting but the response shape is incomplete</message>
                <header>
                    <name>Location</name>
                    <unexpected>not allowed</unexpected>
                    <value>http://www.legislation.gov.uk/uksi/2025/1059/made</value>
                </header>
            </error>
            """;

        Error error = Error.parse(xml);

        assertEquals(307, error.statusCode);
        assertEquals("Redirecting but the response shape is incomplete", error.message);
        assertNotNull(error.header);
        assertEquals("Location", error.header.name);
    }

    @Test
    @DisplayName("Error.parse preserves link href and text when links are separated by punctuation")
    void parseMixedContentBetweenLinksPreservesLinkData() throws JacksonException {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>That item of legislation isn't available on this site and we can't find any references to it either. Perhaps the link is slightly wrong.</message>
                <link href="http://www.legislation.gov.uk/ukpga/2024">Browse for other legislation of this type from 2024</link>,
                <link href="http://www.legislation.gov.uk/ukpga">Browse for other legislation of this type</link>
            </error>
            """;

        Error error = Error.parse(xml);

        assertNotNull(error.links);
        assertEquals(2, error.links.size());
        assertEquals("http://www.legislation.gov.uk/ukpga/2024", error.links.get(0).href);
        assertEquals("Browse for other legislation of this type from 2024", error.links.get(0).text);
        assertEquals("http://www.legislation.gov.uk/ukpga", error.links.get(1).href);
        assertEquals("Browse for other legislation of this type", error.links.get(1).text);
        assertEquals("That item of legislation isn't available on this site and we can't find any references to it either. Perhaps the link is slightly wrong.", error.message);
    }

    @Test
    @DisplayName("Error.parse(stream) handles mixed content between repeated links")
    void parseMixedContentBetweenLinksFromStream() throws IOException {
        String xml = """
            <error xmlns="">
                <status-code>404</status-code>
                <message>That item of legislation isn't available on this site and we can't find any references to it either. Perhaps the link is slightly wrong.</message>
                <link href="http://www.legislation.gov.uk/ukpga/2024">Browse for other legislation of this type from 2024</link>,
                <link href="http://www.legislation.gov.uk/ukpga">Browse for other legislation of this type</link>
            </error>
            """;

        try (PushbackInputStream input = TestMarkLogic.asStream(xml)) {
            var maybeError = Error.parse(input);

            assertTrue(maybeError.isPresent());
            Error error = maybeError.orElseThrow();
            assertNotNull(error.links);
            assertEquals(2, error.links.size());
            assertEquals("http://www.legislation.gov.uk/ukpga/2024", error.links.get(0).href);
            assertEquals("Browse for other legislation of this type from 2024", error.links.get(0).text);
            assertEquals("http://www.legislation.gov.uk/ukpga", error.links.get(1).href);
            assertEquals("Browse for other legislation of this type", error.links.get(1).text);
        }
    }

    @Test
    @DisplayName("Error.parse rejects CLML responses")
    void parseRejectsClmlXml() throws IOException {
        ClassPathResource resource = new ClassPathResource("ukpga_2023_29/ukpga-2023-29-2024-11-01.xml");
        String xml = resource.getContentAsString(StandardCharsets.UTF_8);

        assertThrows(JacksonException.class, () -> Error.parse(xml));
    }

    @Test
    @DisplayName("Error.parse returns an Error object when status code is missing")
    void parseMissingStatusXml() throws JacksonException {
        String xml = """
            <error xmlns="">
                <message>Missing status code</message>
            </error>
            """;

        Error error = Error.parse(xml);

        assertEquals(0, error.statusCode);
        assertEquals("Missing status code", error.message);
        assertNull(error.header);
    }

    @Test
    @DisplayName("Error.classifyRoot(stream) can be followed by Error.parseAssumingError(stream)")
    void classifyRootThenParseAssumingErrorFromStream() throws IOException {
        String xml = """
            <error xmlns="">
                <status-code>307</status-code>
                <message>Redirecting</message>
                <header>
                    <name>Location</name>
                    <value>http://www.legislation.gov.uk/uksi/2025/1059/made</value>
                </header>
            </error>
            """;

        try (PushbackInputStream input = TestMarkLogic.asStream(xml)) {
            assertEquals(Error.RootClassification.ERROR, Error.classifyRoot(input));

            Error error = Error.parseAssumingError(input);

            assertEquals(307, error.statusCode);
            assertNotNull(error.header);
            assertEquals("Location", error.header.name);
            assertEquals("http://www.legislation.gov.uk/uksi/2025/1059/made", error.header.value);
        }
    }

    @Test
    @DisplayName("Error.parse succeeds on MarkLogic redirect errors")
    void parseValidRedirectErrorXml() throws JacksonException {
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

        Error error = Error.parse(xml);

        assertEquals(307, error.statusCode);
        assertEquals("Redirecting you to The East Yorkshire Solar Farm (Correction) Order 2025 as made", error.message);
        assertNotNull(error.header);
        assertEquals("Location", error.header.name);
        assertEquals("http://www.legislation.gov.uk/uksi/2025/1059/made", error.header.value);
    }

}
