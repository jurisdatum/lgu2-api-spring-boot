package uk.gov.legislation.api.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import uk.gov.legislation.Application;
import uk.gov.legislation.api.responses.ExtendedMetadata;
import uk.gov.legislation.converters.ExtendedMetadataConverter;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static uk.gov.legislation.api.test.ExtendedMetadataTest.provide;


public class ExtendedMetadataTestRedo {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        Simplify simplifier = ctx.getBean(Simplify.class);
        for (String id : provide().toList()) {
            redo(id, simplifier);
        }
        SpringApplication.exit(ctx);
    }

    static Path makePath(String id, String extra) {
        String resource = id.replace('/', '_') +
            "/" + id.replace('/', '-') + "-metadata" + extra;
        return Path.of("src/test/resources", resource);
    }

    static Path makePathForClml(String id) {
        return makePath(id, ".xml");
    }
    static String readClml(String id) throws IOException {
        Path path = makePathForClml(id);
        return Files.readString(path);
    }

    static Path makePathForSimpleXml(String id) {
        return makePath(id, "-simple.xml");
    }
    static String readSimpleXml(String id) throws IOException {
        Path path = makePathForSimpleXml(id);
        return Files.readString(path);
    }
    static void writeSimpleXml(String id, String xml) throws IOException {
        Path path = makePathForSimpleXml(id);
        Files.writeString(path, xml);
    }

    static Path makePathForJson(String id) {
        return makePath(id, ".json");
    }
    static String readJson(String id) throws IOException {
        Path path = makePathForJson(id);
        return Files.readString(path);
    }
    static void writeJson(String id, String json) throws IOException {
        Path path = makePathForJson(id);
        Files.writeString(path, json);
    }

    static String simplify(Simplify simplifier, String clml) throws SaxonApiException, TransformerException {
        Simplify.Parameters params = new Simplify.Parameters(false, false);
        String simple = simplifier.transform(clml, params);
        return UnappliedEffectsTest.indent(simple);
    }

    static String toJson(String simple) throws JsonProcessingException {
        Metadata meta1 = Contents.parse(simple).meta;
        ExtendedMetadata meta2 = ExtendedMetadataConverter.convert(meta1);
        return TransformHelper.MAPPER.writeValueAsString(meta2);
    }

    private static void redo(String id, Simplify simplifier) throws Exception {
        String xml = readClml(id);
        String simple = simplify(simplifier, xml);
        String expected;
        try {
            expected = readSimpleXml(id);
        } catch (IOException e) {
            expected = null;
        }
        if (simple.equals(expected)) {
            System.out.println("skipping XML " + id);
        } else {
            System.out.println("redoing XML " + id);
            writeSimpleXml(id, simple);
        }
        String json = toJson(simple);
        try {
            expected = readJson(id);
        } catch (IOException e) {
            expected = null;
        }
        if (json.equals(expected)) {
            System.out.println("skipping JSON " + id);
        } else {
            System.out.println("redoing JSON " + id);
            writeJson(id, json);
        }
    }

}
