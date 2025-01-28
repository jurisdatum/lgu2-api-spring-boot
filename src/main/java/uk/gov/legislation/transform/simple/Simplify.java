package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class Simplify {

    private static final String STYLESHEET = "/transforms/simplify.xsl";

    private final XsltExecutable executable;

    public Simplify() {
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

    private void transform(Source clml, Serializer destination, Parameters params) throws SaxonApiException {
        XsltTransformer transformer = executable.load();
        transformer.setParameter(new QName("is-fragment"), new XdmAtomicValue(params.isFragment()));
        transformer.setParameter(new QName("include-contents"), new XdmAtomicValue(params.includeContents()));
        transformer.setSource(clml);
        transformer.setDestination(destination);
        transformer.transform();
    }

    public record Parameters(boolean isFragment, boolean includeContents) {}

    public String transform(XdmNode clml, Parameters params) throws SaxonApiException {
        try (var output = new ByteArrayOutputStream()) {
            Serializer destination = executable.getProcessor().newSerializer(output);
            transform(clml.asSource(), destination, params);
            return output.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TransformationException("Error processing the transformation input/output", e);
        }
    }

    public String transform(String clml, Parameters params) throws SaxonApiException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Source source = new StreamSource(input);
            Serializer destination = executable.getProcessor().newSerializer(output);
            transform(source, destination, params);

            return output.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TransformationException("Error processing the transformation input/output", e);
        }
    }

    public Contents contents(String clml) throws SaxonApiException, JsonProcessingException {
        Parameters params = new Parameters(false, true);
        String simplified = transform(clml, params);
        return Contents.parse(simplified);
    }

    /* metadata */

    private Metadata metadata(XdmNode clml, boolean isFragment) throws SaxonApiException, JsonProcessingException {
        Parameters params = new Parameters(isFragment, false);
        String simplified = transform(clml, params);
        return Contents.parse(simplified).meta;
    }
    private Metadata metadata(String clml, boolean isFragment) throws SaxonApiException, JsonProcessingException {
        Parameters params = new Parameters(isFragment, false);
        String simplified = transform(clml, params);
        return Contents.parse(simplified).meta;
    }

    public Metadata extractDocumentMetadata(XdmNode clml) throws SaxonApiException, JsonProcessingException {
        return metadata(clml, false);
    }
    public Metadata extractDocumentMetadata(String clml) throws SaxonApiException, JsonProcessingException {
        return metadata(clml, false);
    }

    public Metadata extractFragmentMetadata(XdmNode clml) throws SaxonApiException, JsonProcessingException {
        return metadata(clml, true);
    }
    public Metadata extractFragmentMetadata(String clml) throws SaxonApiException, JsonProcessingException {
        return metadata(clml, true);
    }

}
