package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Service
public class Clml2Pdf {

    private static final String STYLESHEET = "/transforms/clml2xslfo/legislation/fo/legislation_schema_FO.xslt";

    private final XsltExecutable executable;

    private final FopFactory factory;

    public Clml2Pdf() {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        Source source;
        try {
            String systemId = this.getClass().getResource(STYLESHEET).toURI().toASCIIString();
            source = new StreamSource(systemId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        }
        FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
        builder.setStrictFOValidation(false);  // our XSL-FO contains duplicate IDs
        factory = builder.build();
    }

    private Fop makeFop(OutputStream pdf) {
        try {
            return factory.newFop(org.apache.xmlgraphics.util.MimeConstants.MIME_PDF, pdf);
        } catch (FOPException e) {
            throw new RuntimeException(e);
        }
    }

    private Destination makeFopDestination(OutputStream pdf) {
        Fop fop = makeFop(pdf);
        try {
            return new SAXDestination(fop.getDefaultHandler());
        } catch (FOPException e) {
            throw new RuntimeException(e);
        }
    }

    public void transform(InputStream clml, OutputStream pdf) throws SaxonApiException {
        Source source = new StreamSource(clml);
        Destination destination = makeFopDestination(pdf);
        XsltTransformer transform = executable.load();
        transform.setSource(source);
        transform.setDestination(destination);
        transform.transform();
    }

    /* for testing */

    String clml2xslFo(InputStream clml) throws SaxonApiException {
        Source source = new StreamSource(clml);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Serializer serializer = executable.getProcessor().newSerializer(baos);
        XsltTransformer transform = executable.load();
        transform.setSource(source);
        transform.setDestination(serializer);
        transform.transform();
        return baos.toString(StandardCharsets.UTF_8);
    }

    void xslFo2pdf(String fo, OutputStream pdf) throws Exception {
        Fop fop = makeFop(pdf);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source src = new StreamSource(new StringReader(fo));
        Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
    }

}
