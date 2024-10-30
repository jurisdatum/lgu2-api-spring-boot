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

    private static final String stylesheet = "/transforms/akn2html/akn2html.xsl";

//    private static final String CSS_PATH_ENV_VAR = "CSS_PATH";
//    private static final String IMAGES_PATH = "/static/lgu1/images/";

    private final XsltExecutable executable;

    public Akn2Html() {
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

    private void transform(Source akn, Destination html, boolean standalone) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setParameter(new QName("standalone"), new XdmAtomicValue(standalone));
//        String cssPath = System.getenv(CSS_PATH_ENV_VAR);
//        if (cssPath != null)
//            transform.setParameter(new QName("css-path"), new XdmAtomicValue(cssPath));
//        transform.setParameter(new QName("images-path"), new XdmAtomicValue(IMAGES_PATH));
        transform.setSource(akn);
        transform.setDestination(html);
        transform.transform();
    }

    static final Properties Indent = new Properties();
    static final Properties DontIndent = new Properties();
    static {
        Indent.setProperty(Serializer.Property.OMIT_XML_DECLARATION.toString(), "yes");
        DontIndent.setProperty(Serializer.Property.OMIT_XML_DECLARATION.toString(), "yes");
        Indent.setProperty(Serializer.Property.INDENT.toString(), "yes");
        DontIndent.setProperty(Serializer.Property.INDENT.toString(), "no");
    }

    public String transform(XdmNode akn, boolean standalone) throws SaxonApiException {
        StringWriter html = new StringWriter();
        Serializer serializer = akn.getProcessor().newSerializer(html);
        serializer.setOutputProperties(standalone ? Indent : DontIndent);
        transform(akn.asSource(), serializer, standalone);
        return html.toString();
    }

}
