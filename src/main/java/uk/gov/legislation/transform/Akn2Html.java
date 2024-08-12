package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Akn2Html {

    private static final String stylesheet = "/transforms/akn2html/akn2html.xsl";

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

    private void transform(Source akn, Destination html) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setSource(akn);
        transform.setDestination(html);
        transform.transform();
    }

    public String transform(XdmNode akn) throws SaxonApiException {
        StringWriter html = new StringWriter();
        Result result = new StreamResult(html);
        Destination destination = Helper.makeDestination(result, Helper.html5properties);
        transform(akn.asSource(), destination);
        try {
            html.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return html.toString();
    }

}
