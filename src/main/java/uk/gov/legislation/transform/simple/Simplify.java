package uk.gov.legislation.transform.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;
import uk.gov.legislation.transform.Helper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class Simplify {

    private static final String STYLESHEET = "/transforms/simplify/legislation.xsl";

    private final XsltExecutable executable;

    public Simplify() {
        Source source;
        try {
            String systemId = getClass().getResource(STYLESHEET).toURI().toASCIIString();
            source = new StreamSource(systemId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void transform(Source clml, Destination destination, Parameters params) throws SaxonApiException {
        Xslt30Transformer transformer = executable.load30();
        Map<QName, XdmValue> params2 = new LinkedHashMap<>();
        params2.put(new QName("is-fragment"), new XdmAtomicValue(params.isFragment()));
        params2.put(new QName("include-contents"), new XdmAtomicValue(params.includeContents()));
        transformer.setStylesheetParameters(params2);
        transformer.transform(clml, destination);
    }

    public record Parameters(boolean isFragment, boolean includeContents) {}

    public String transform(XdmNode clml, Parameters params) throws SaxonApiException {
        var output = new ByteArrayOutputStream();
        Serializer destination = executable.getProcessor().newSerializer(output);
        transform(clml.asSource(), destination, params);
        return output.toString(StandardCharsets.UTF_8);
    }

    public String transform(String clml, Parameters params) throws SaxonApiException {
        ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Source source = new StreamSource(input);
        Serializer destination = executable.getProcessor().newSerializer(output);
        transform(source, destination, params);
        return output.toString(StandardCharsets.UTF_8);
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
