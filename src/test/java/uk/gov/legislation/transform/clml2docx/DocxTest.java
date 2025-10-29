package uk.gov.legislation.transform.clml2docx;

import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.transform.TransformHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class DocxTest {

    public static Stream<String> provide() {
        return Stream.of(
            "ukpga/2023/29/2024-11-01"
        );
    }

    public static class Delegate implements uk.gov.legislation.transform.clml2docx.Delegate {

        // Minimal 1x1 placeholders so width/height metadata stays consistent during tests.
        private static final byte[] DUMMY_GIF = createDummyImage("gif");
        private static final byte[] DUMMY_JPEG = createDummyImage("jpg");

        private static byte[] createDummyImage(String format) {
            BufferedImage pixel = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                if (!ImageIO.write(pixel, format, out)) {
                    throw new IllegalStateException("No ImageIO writer for format " + format);
                }
                return out.toByteArray();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to prepare dummy " + format + " resource", e);
            }
        }

        @Override
        public Resource fetch(String uri) {
            String lower = uri.toLowerCase(Locale.ROOT);
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                return new Resource(DUMMY_JPEG, "image/jpeg");
            }
            return new Resource(DUMMY_GIF, "image/gif");
        }

        @Override
        public void setConversionParameters(Map<String, String> conversionParameters) {
        }

        @Override
        public Map<String, String> getConversionParameters() {
            return Map.of();
        }
    }

    private final Clml2Docx transform = new Clml2Docx(new Delegate());

    public DocxTest() throws IOException { }

    public static final String DOCX_XML_EXT = "docx.xml";

    @ParameterizedTest
    @MethodSource("provide")
    void one(String id) throws IOException, SaxonApiException {
        String clml = TransformHelper.read(id, "xml");
        byte[] docx = transform.transform(new ByteArrayInputStream(clml.getBytes(StandardCharsets.UTF_8)));
        String actual = extractDocumentXml(docx);
        String expected = TransformHelper.read(id, DOCX_XML_EXT);
        Assertions.assertEquals(expected, actual);
    }

    public static String extractDocumentXml(byte[] docxBytes) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(docxBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && "word/document.xml".equals(entry.getName())) {
                    return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

}
