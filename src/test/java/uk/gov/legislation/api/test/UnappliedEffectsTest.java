package uk.gov.legislation.api.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.Application;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.endpoints.document.service.EffectsConverter;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.transform.simple.UnappliedEffect;
import uk.gov.legislation.util.Effects;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.legislation.api.test.TransformTest.read;

@SpringBootTest(classes = Application.class)
class UnappliedEffectsTest {

    private final Simplify simplifier;

    @Autowired
    UnappliedEffectsTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    static Stream<String> provide() {
        return Stream.of("ukpga/2000/8/section/91");
    }

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    @ParameterizedTest
    @MethodSource("provide")
    void simplify(String id) throws Exception {
        String clml = read(id, ".xml");
        String actual = indent(simplifier.transform(clml));
        String expected = read(id, "-simplified.xml");
        Assertions.assertEquals(expected, actual);
    }

    private String indent(String xml) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StreamSource source = new StreamSource(new StringReader(xml));
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }

    @ParameterizedTest
    @MethodSource("provide")
    void raw(String id) throws Exception {
        String clml = read(id, ".xml");
        Metadata meta = simplifier.metadata(clml);
        String actual = mapper.writeValueAsString(meta.rawEffects());
        String expected = read(id, "-effects-raw.json");
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void sorted(String id) throws Exception {
        String clml = read(id, ".xml");
        Metadata meta = simplifier.metadata(clml);
        List<UnappliedEffect> effects = meta.rawEffects();
        Effects.sort(effects);
        String actual = mapper.writeValueAsString(effects);
        String expected = read(id, "-effects-sorted.json");
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void filtered(String id) throws Exception {
        String clml = read(id, ".xml");
        Metadata meta = simplifier.metadata(clml);
        Set<String> ids = meta.ancestors().stream().map(l -> l.id).collect(Collectors.toSet());
        meta.descendants().stream().map(l -> l.id).forEach(ids::add);
        List<UnappliedEffect> effects = Effects.removeThoseWithNoRelevantSection(meta.rawEffects(), ids);
        String actual = mapper.writeValueAsString(effects);
        String expected = read(id,"-effects-filtered.json");
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provide")
    void converted(String id) throws Exception {
        String clml = read(id, ".xml");
        Metadata meta = simplifier.metadata(clml);
        List<Effect> effects = EffectsConverter.convert(meta.rawEffects());
        String actual = mapper.writeValueAsString(effects);
        String expected = read(id, "-effects-converted.json");
        Assertions.assertEquals(expected, actual);
    }

}
