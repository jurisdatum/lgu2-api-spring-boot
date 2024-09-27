package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

public class Akn2Html {

    private static final String stylesheet = "/transforms/akn2html/akn2html.xsl";

    private static final String CSS_PATH_ENV_VAR = "CSS_PATH";

    private static class Importer implements URIResolver {
        @Override public Source resolve(String href, String base) throws TransformerException {
            InputStream file = this.getClass().getResourceAsStream("/transforms/akn2html/" + href);
            return new StreamSource(file, href);
        }
    }

    private final XsltExecutable executable;

    public Akn2Html() {
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        compiler.setURIResolver(new Importer());
        InputStream stream = this.getClass().getResourceAsStream(stylesheet);
        Source source = new StreamSource(stream, "akn2html.xsl");
        try {
            executable = compiler.compile(source);
        } catch (SaxonApiException e) {
            throw new RuntimeException(e);
        } finally {
            try { stream.close(); } catch (IOException e) { }
        }
    }

    private void transform(Source akn, Destination html, boolean standalone) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setParameter(new QName("standalone"), new XdmAtomicValue(standalone));
        String cssPath = System.getenv(CSS_PATH_ENV_VAR);
        if (cssPath != null)
            transform.setParameter(new QName("css-path"), new XdmAtomicValue(cssPath));
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
