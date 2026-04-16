package uk.gov.legislation.data.marklogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Error {

    private static final Logger logger = LoggerFactory.getLogger(Error.class);

    private static final XMLInputFactory factory = XMLInputFactory.newFactory();

    /**
     * Result of classifying the root element of a MarkLogic response.
     *
     * <p>Note: {@link #MALFORMED} is only returned by the {@code String} overload of
     * {@link #classifyRoot}. The {@code PushbackInputStream} overload is best-effort: it
     * operates on a fixed-size peek buffer, so an {@link javax.xml.stream.XMLStreamException}
     * on the sample is treated as {@link #OTHER} rather than {@link #MALFORMED} — the
     * full stream may still be valid XML.</p>
     */
    public enum RootClassification { ERROR, OTHER, MALFORMED }

    public int statusCode;

    public String message;

    public Header header;

    public List<Link> links;

    public static class Header {

        public String name;

        public String value;

    }

    public static class Link {

        public String href;

        public String text;

    }

    /**
     * Parses an {@code <error>} payload from a String.
     *
     * <p>Unlike {@link #parseAssumingError(PushbackInputStream)}, this method validates that
     * the root element is {@code <error>} and throws {@link tools.jackson.core.exc.StreamReadException}
     * if it is not. Use {@link #classifyRoot(String)} first if the root is uncertain.</p>
     */
    public static Error parse(String xml) throws JacksonException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                return parseError(reader);
            } finally {
                closeQuietly(reader);
            }
        } catch (IOException | XMLStreamException e) {
            throw new StreamReadException(null, "Failed to parse error response: " + e.getMessage(), e);
        }
    }

    public static RootClassification classifyRoot(String xml) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                return classifyRoot(reader);
            } finally {
                closeQuietly(reader);
            }
        } catch (IOException | XMLStreamException e) {
            return RootClassification.MALFORMED;
        }
    }

    public static RootClassification classifyRoot(PushbackInputStream input) throws IOException {
        byte[] peek = input.readNBytes(1024);
        if (peek.length == 0)
            return RootClassification.OTHER;
        try (ByteArrayInputStream sample = new ByteArrayInputStream(peek)) {
            XMLStreamReader reader = factory.createXMLStreamReader(sample);
            try {
                return classifyRoot(reader);
            } finally {
                closeQuietly(reader);
            }
        } catch (XMLStreamException e) {
            // Stream classification is best-effort: a parse failure on the sample does not
            // mean the full document is malformed, so fall through to OTHER.
            return RootClassification.OTHER;
        } finally {
            input.unread(peek, 0, peek.length);
        }
    }

    public static Optional<Error> parse(PushbackInputStream input) throws IOException {
        RootClassification classification = classifyRoot(input);
        if (classification != RootClassification.ERROR)
            return Optional.empty();
        return Optional.of(parseAssumingError(input));
    }

    /**
     * Parses an <error> payload without first peeking at the root element.
     * Callers must have already verified that the root is <error>, e.g. via {@link #classifyRoot(PushbackInputStream)}.
     */
    public static Error parseAssumingError(PushbackInputStream input) throws IOException {
        try {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                return parseError(reader);
            } finally {
                closeQuietly(reader);
            }
        } catch (XMLStreamException e) {
            throw new IOException("Failed to parse error response", e);
        }
    }

    private static RootClassification classifyRoot(XMLStreamReader reader) throws XMLStreamException {
        int event = reader.nextTag();
        return event == XMLStreamConstants.START_ELEMENT && "error".equals(reader.getLocalName())
            ? RootClassification.ERROR
            : RootClassification.OTHER;
    }

    private static Error parseError(XMLStreamReader reader) throws XMLStreamException, StreamReadException {
        if (classifyRoot(reader) != RootClassification.ERROR)
            throw new StreamReadException(null, "Expected <error> root element");

        Error error = new Error();
        int event;
        while (reader.hasNext()) {
            event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "status-code" -> parseStatusCode(reader, error);
                    case "message" -> error.message = reader.getElementText();
                    case "header" -> error.header = parseHeader(reader);
                    case "link" -> addLink(error, parseLink(reader));
                    default -> {
                        logger.debug("Unexpected element <{}> under <error>; skipping", reader.getLocalName());
                        skipElement(reader);
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "error".equals(reader.getLocalName())) {
                break;
            }
        }
        return error;
    }

    private static void parseStatusCode(XMLStreamReader reader, Error error) throws XMLStreamException {
        String text = reader.getElementText().trim();
        try {
            error.statusCode = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            logger.warn("Unparseable <status-code> value: {}", text);
        }
    }

    private static Header parseHeader(XMLStreamReader reader) throws XMLStreamException {
        Header header = new Header();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "name" -> header.name = reader.getElementText();
                    case "value" -> header.value = reader.getElementText();
                    default -> {
                        logger.debug("Unexpected element <{}> under <header>; skipping", reader.getLocalName());
                        skipElement(reader);
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "header".equals(reader.getLocalName())) {
                return header;
            }
        }
        return header;
    }

    private static Link parseLink(XMLStreamReader reader) throws XMLStreamException {
        Link link = new Link();
        link.href = reader.getAttributeValue(null, "href");
        StringBuilder text = new StringBuilder();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA || event == XMLStreamConstants.SPACE) {
                text.append(reader.getText());
            } else if (event == XMLStreamConstants.START_ELEMENT) {
                logger.debug("Unexpected element <{}> under <link>; skipping", reader.getLocalName());
                skipElement(reader);
            } else if (event == XMLStreamConstants.END_ELEMENT && "link".equals(reader.getLocalName())) {
                link.text = text.toString();
                return link;
            }
        }
        link.text = text.toString();
        return link;
    }

    private static void addLink(Error error, Link link) {
        if (error.links == null)
            error.links = new ArrayList<>();
        error.links.add(link);
    }

    private static void skipElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        while (depth > 0 && reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) depth++;
            else if (event == XMLStreamConstants.END_ELEMENT) depth--;
        }
    }

    private static void closeQuietly(XMLStreamReader reader) {
        if (reader == null)
            return;
        try {
            reader.close();
        } catch (XMLStreamException ignored) {
        }
    }

}
