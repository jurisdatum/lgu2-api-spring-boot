package uk.gov.legislation.transform.simple;

import uk.gov.legislation.transform.TransformTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class UnappliedEffectsHelper {

    public static String read(String resource) throws IOException {
        String content;
        try (var input = TransformTest.class.getResourceAsStream(resource)) {
            Objects.requireNonNull(input);
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        return content;
    }

    static String makeResourceName(String id, String ext) {
        String dir = id.replace('/', '_') + "/";
        String file = id.replace('/', '-') + ext;
        return  "/" +  dir + file;
    }

    static String read(String id, String ext) throws IOException {
        String resource = makeResourceName(id, ext);
        return read(resource);
    }

    static void write(String resource, String content) throws IOException {
        Path path = Path.of("src/test/resources", resource.substring(1));
        Files.writeString(path, content);
    }

    static void write(String id, String ext, String content) throws IOException {
        String resource = makeResourceName(id, ext);
        write(resource, content);
    }

}
