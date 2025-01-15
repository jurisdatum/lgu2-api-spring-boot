package uk.gov.legislation.transform.simple.effects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Objects;

@Service("effectsSimplifier")
public class EffectsSimplifier {

    private static final String STYLESHEET = "/transforms/simplify/feed.xsl";

    private final XsltExecutable executable;

    public EffectsSimplifier() {
        String systemId;
        try {
            systemId = Objects.requireNonNull(getClass().getResource(STYLESHEET)).toURI().toASCIIString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Source source = new StreamSource(systemId);
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String simpify(String atom) throws SaxonApiException {
        Xslt30Transformer transformer = executable.load30();
        StringWriter simple = new StringWriter();
        Source source = new StreamSource(new StringReader(atom));
        Serializer destination = transformer.newSerializer(simple);
        transformer.transform(source, destination);
        return simple.toString();
    }

    private static final XmlMapper Mapper = (XmlMapper) new XmlMapper()
        .registerModules(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public Page parse(String atom) throws SaxonApiException, JsonProcessingException {
        String simple = simpify(atom);
        return Mapper.readValue(simple, Page.class);
    }

}
