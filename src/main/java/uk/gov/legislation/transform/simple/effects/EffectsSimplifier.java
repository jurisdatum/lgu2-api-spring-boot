package uk.gov.legislation.transform.simple.effects;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import uk.gov.legislation.transform.Helper;
import uk.gov.legislation.transform.simple.SimpleXmlMapper;

@Service("effectsSimplifier")
public class EffectsSimplifier {

    private static final String STYLESHEET = "/transforms/simplify/feed.xsl";

    private final XsltExecutable executable;

    public EffectsSimplifier() {
        String systemId;
        try {
            systemId =
                    Objects.requireNonNull(getClass().getResource(STYLESHEET))
                            .toURI()
                            .toASCIIString();
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

    public Page parse(String atom) throws SaxonApiException, JacksonException {
        String simple = simpify(atom);
        return SimpleXmlMapper.INSTANCE.readValue(simple, Page.class);
    }
}
