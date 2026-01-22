package uk.gov.legislation.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.gov.legislation.util.Links;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TransformHelper {

    private static String makeResourceName(String id, String ext) {
        Links.Components comps = Links.parse(id);
        String dir = comps.type() + "_" + comps.year().replace('/', '_') + "_" + comps.number() + "/";
        String file = comps.type() + "-" + comps.year().replace('/', '-') + "-" + comps.number();
        if (comps.fragment().isPresent())
            file += "-" + comps.fragment().get().replace('/', '-');
        else if (comps.isContents())
            file += "-contents";
        if (comps.version().isPresent())
            file += "-" + comps.version().get();
        if (comps.language().isPresent())
            file += "-" + comps.language().get();
        file += "." + ext;
        return  "/" +  dir + file;
    }

    public static InputStream open(String id, String ext) {
        String resource = makeResourceName(id, ext);
        return TransformTest.class.getResourceAsStream(resource);
    }
    public static String read(String id, String ext) throws IOException {
        String resource = makeResourceName(id, ext);
        String content;
        try (var input = TransformTest.class.getResourceAsStream(resource)) {
            Objects.requireNonNull(input);
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        return content;
    }

    public static void write(String id, String ext, String content) throws IOException {
        String resource = makeResourceName(id, ext);
        Path path = Path.of("src/test/resources", resource.substring(1));
        Files.writeString(path, content);
    }

    public static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

}
