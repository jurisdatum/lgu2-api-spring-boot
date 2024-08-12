package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Clml2Akn {

    private static final String stylesheet = "/transforms/clml2akn/clml2akn.xsl";

    private static class Importer implements URIResolver {
        @Override public Source resolve(String href, String base) throws TransformerException {
            InputStream file = this.getClass().getResourceAsStream("/transforms/clml2akn/" + href);
            return new StreamSource(file, href);
        }
    }

    private final XsltExecutable executable;

    public Clml2Akn() {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        compiler.setURIResolver(new Importer());
        InputStream stream = this.getClass().getResourceAsStream(stylesheet);
        Source source = new StreamSource(stream, "clml2akn.xsl");
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

    public static String serialize(XdmNode akn) throws SaxonApiException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        Destination destination = Helper.makeDestination(result, Helper.aknProperties);
        Helper.processor.writeXdmValue(akn, destination);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

}
