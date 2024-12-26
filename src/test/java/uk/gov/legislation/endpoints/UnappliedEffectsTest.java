package uk.gov.legislation.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.document.responses.Effect;
import uk.gov.legislation.endpoints.document.service.EffectsConverter;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.transform.simple.effects.UnappliedEffect;
import uk.gov.legislation.util.Effects;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@SpringBootTest
class UnappliedEffectsTest {

    private final Simplify simplifier;

    @Autowired
    UnappliedEffectsTest(Simplify simplifier) {
        this.simplifier = simplifier;
    }

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT);

    private String getClml() throws IOException {
        return TransformTest.read("/ukpga-2000-8-section-91.xml");
    }

    @Test
    void simplify() throws Exception {
        String clml = getClml();
        String actual = indent(simplifier.transform(clml));
        String expected = TransformTest.read("/ukpga-2000-8-section-91_simplified.xml");
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

    @Test
    void raw() throws Exception {
        String clml = getClml();
        Metadata meta = simplifier.metadata(clml);
        String actual = mapper.writeValueAsString(meta.rawEffects());
        String expected = TransformTest.read("/ukpga-2000-8-section-91-effects-raw.json");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void filtered() throws Exception {
        String clml = getClml();
        Metadata meta = simplifier.metadata(clml);
        List<UnappliedEffect> effects = Effects.removeThoseWithNoRelevantSection(meta.rawEffects(), meta.getInternalIds());
        String actual = mapper.writeValueAsString(effects);
        String expected = TransformTest.read("/ukpga-2000-8-section-91-effects-filtered.json");
        Assertions.assertEquals(expected, actual);
    }

        @Test
    void converted() throws Exception {
        String clml = getClml();
        Metadata meta = simplifier.metadata(clml);
        List<Effect> effects = EffectsConverter.convert(meta.rawEffects());
        String actual = mapper.writeValueAsString(effects);
        String expected = TransformTest.read("/ukpga-2000-8-section-91-effects-converted.json");
        Assertions.assertEquals(expected, actual);
    }

}
