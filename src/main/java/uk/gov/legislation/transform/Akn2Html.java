package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Component;
import uk.gov.legislation.config.Configuration;
import uk.gov.legislation.exceptions.XSLTCompilationException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

@Component
public class Akn2Html {

    private final XsltExecutable executable;
    private final Configuration stylSheetAknPath;

    public Akn2Html(Configuration stylSheetAknPath) {
        this.stylSheetAknPath = stylSheetAknPath;
        this.executable = compileXslt();
    }

    public String getStylSheetAknPath() {
        return stylSheetAknPath.getStylesheetAknPath();
    }

    private XsltExecutable compileXslt() {
        return wrapWithRuntimeException(() -> {
            XsltCompiler compiler = Helper.processor.newXsltCompiler();
            String systemId = Objects.requireNonNull(this.getClass().getResource(getStylSheetAknPath()).toURI().toASCIIString());
            Source source = new StreamSource(systemId);
            return compiler.compile(source);
        });
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

   /**
     Helper method to wrap exceptions and throw RuntimeException
   */
    private <T> T wrapWithRuntimeException(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (URISyntaxException | SaxonApiException e) {
            throw new XSLTCompilationException(e.getMessage());
        }
    }

   /**
     Functional interface for suppliers that can throw checked exceptions
    */
    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws URISyntaxException, SaxonApiException;
    }
}

