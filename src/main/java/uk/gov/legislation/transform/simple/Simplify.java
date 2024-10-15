package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.*;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class Simplify {

    private static final String stylesheet = "/transforms/simplify.xsl";

    private final XsltExecutable executable;

    public Simplify() {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        Source source;
        try {
            String systemId = this.getClass().getResource(stylesheet).toURI().toASCIIString();
            source = new StreamSource(systemId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void transform(Source clml, Destination destination) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setSource(clml);
        transform.setDestination(destination);
        transform.transform();
    }

    public String transform(String clml) throws SaxonApiException {
        ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Source source = new StreamSource(input);
        Serializer destination = executable.getProcessor().newSerializer(output);
        transform(source, destination);
        return output.toString(StandardCharsets.UTF_8);
    }

    public Contents contents(String clml) throws SaxonApiException, JsonProcessingException {
        String simplified = transform(clml);
        return Contents.parse(simplified);
    }

}
