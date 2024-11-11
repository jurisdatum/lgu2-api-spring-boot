package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Component;
import uk.gov.legislation.config.Configuration;
import uk.gov.legislation.exceptions.InvalidURISyntaxException;
import uk.gov.legislation.exceptions.XSLTCompilationException;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class Simplify {

    private final Configuration stylesheetConfig;
    private final XsltExecutable executable;

    public Simplify(Configuration stylesheetConfig) {
        this.stylesheetConfig = stylesheetConfig;
        this.executable = compileXslt();
    }
    public String processDocument() {
      return stylesheetConfig.getStylesheetPath();
    }

    private XsltExecutable compileXslt() {
        try {
            String systemId = getStylesheetURI();
            Source source = new StreamSource(systemId);
            return compileXsltSource(source);
        } catch (URISyntaxException | SaxonApiException e) {
            throw new XSLTCompilationException("Error compiling XSLT");
        }
    }

    private String getStylesheetURI() throws URISyntaxException {
        URI uri = Optional.ofNullable(this.getClass().getResource(processDocument()))
                .map(resource -> {
                    try {
                        return resource.toURI();
                    }
                    catch(URISyntaxException e) {
                        throw new InvalidURISyntaxException("Invalid URI syntax for stylesheet", e);
                    }
                })
                .orElseThrow(() -> new URISyntaxException(processDocument(), "Stylesheet not found"));
        return uri.toASCIIString();
    }

    private XsltExecutable compileXsltSource(Source source) throws SaxonApiException {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        return compiler.compile(source);
    }

    private void transform(Source clml, Serializer destination) throws SaxonApiException {
        XsltTransformer transformer = executable.load();
        transformer.setSource(clml);
        transformer.setDestination(destination);
        transformer.transform();
    }

    public String transform(String clml) throws SaxonApiException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Source source = new StreamSource(input);
            Serializer destination = executable.getProcessor().newSerializer(output);
            transform(source, destination);

            return output.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SaxonApiException("Error processing the transformation input/output", e);
        }
    }

    public Contents contents(String clml) throws SaxonApiException, JsonProcessingException {
        return parseSimplifiedContent(transform(clml));
    }

    private Contents parseSimplifiedContent(String simplified) throws JsonProcessingException {
        return Contents.parse(simplified);
    }

}
