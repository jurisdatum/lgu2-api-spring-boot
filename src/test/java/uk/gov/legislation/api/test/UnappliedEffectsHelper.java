package uk.gov.legislation.api.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class UnappliedEffectsHelper {
    static String read(String resource) throws IOException {
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
}
