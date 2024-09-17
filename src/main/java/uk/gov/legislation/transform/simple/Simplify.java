package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.*;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Simplify {

    private static final String stylesheet = "/transforms/simplify.xsl";

    private final XsltExecutable executable;

    public Simplify() {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        InputStream stream = this.getClass().getResourceAsStream(stylesheet);
        Source source = new StreamSource(stream, "simplify.xsl");
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        } finally {
            try { stream.close(); } catch (IOException e) { }
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
        Result result = new StreamResult(output);
        Destination destination = Helper.makeDestination(result, new Properties());
        transform(source, destination);
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    public Contents contents(String clml) throws SaxonApiException, JsonProcessingException {
        String simplified = transform(clml);
        return Contents.parse(simplified);
    }

}
