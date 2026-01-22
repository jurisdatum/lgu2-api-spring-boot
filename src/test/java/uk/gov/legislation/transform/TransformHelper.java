package uk.gov.legislation.transform;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.legislation.util.Links;

import java.io.IOException;
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

    public static final ObjectMapper MAPPER = JsonMapper.builder()
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false)
        .configure(SerializationFeature.INDENT_OUTPUT, true)
        .build();

}
