package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Properties;

@Service
public class Akn2Html {

    private static final String STYLESHEET = "/transforms/akn2html/akn2html.xsl";

    private final XsltExecutable executable;

    public Akn2Html() {
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
    }

    private void transform(Source akn, Destination html, boolean standalone) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setParameter(new QName("standalone"), new XdmAtomicValue(standalone));
        transform.setSource(akn);
        transform.setDestination(html);
        transform.transform();
    }

    static final Properties Indent = createProperties(true);
    static final Properties DontIndent = createProperties(false);

    private static Properties createProperties(boolean indent) {
        Properties properties = new Properties();
        properties.setProperty(Serializer.Property.OMIT_XML_DECLARATION.toString(), "yes");
        properties.setProperty(Serializer.Property.INDENT.toString(), indent ? "yes" : "no");
        return properties;
    }

    public String transform(XdmNode akn, boolean standalone) throws SaxonApiException {
        StringWriter html = new StringWriter();
        Serializer serializer = akn.getProcessor().newSerializer(html);
        serializer.setOutputProperties(standalone ? Indent : DontIndent);
        transform(akn.asSource(), serializer, standalone);
        return html.toString();
    }

}

