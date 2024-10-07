package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Clml2Akn {

    private static final String stylesheet = "/transforms/clml2akn/clml2akn.xsl";

    private final XsltExecutable executable;

    public Clml2Akn() {
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

    public XdmNode transform(InputStream clml) throws SaxonApiException {
        Source source = new StreamSource(clml);
        XdmDestination destination = new XdmDestination();
        transform(source, destination);
        return destination.getXdmNode();
    }

    public XdmNode transform(String clml) throws SaxonApiException {
        ByteArrayInputStream stream = new ByteArrayInputStream(clml.getBytes());
        return transform(stream);
    }

    static final Properties Properties = new Properties();
    static {
        Properties.setProperty(Serializer.Property.INDENT.toString(), "yes");
    }

    public static String serialize(XdmNode akn) throws SaxonApiException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = akn.getProcessor().newSerializer(out);
        serializer.setOutputProperties(Properties);
        serializer.serialize(akn.asSource());
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

}
