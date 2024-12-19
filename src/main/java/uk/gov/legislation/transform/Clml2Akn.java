package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import org.springframework.stereotype.Service;
import uk.gov.legislation.config.Configuration;
import uk.gov.legislation.exceptions.InvalidURISyntaxException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@Service
public class ClMl2Akn {

    private final XsltExecutable executable;
    private final Configuration stylSheetClMlPath;
    public String getStylSheetClMlPath() {
        return stylSheetClMlPath.getStylesheetClMlPath();
    }
    public ClMl2Akn(Configuration stylSheetClmlPath) throws SaxonApiException, InvalidURISyntaxException {
        this.stylSheetClMlPath = stylSheetClmlPath;
        XsltCompiler compiler = Helper.processor.newXsltCompiler();
        Source source;
        try {
            String systemId = Objects.requireNonNull(this.getClass().getResource(getStylSheetClMlPath())).toURI().toASCIIString();
            source = new StreamSource(systemId);
        } catch (URISyntaxException e) {
            throw new InvalidURISyntaxException(
                    "Failed to convert the stylesheet path to a valid URI: " + getStylSheetClMlPath(), e);

        }
        try {
            executable = compiler.compile(source);
        } catch (Exception e) {
            throw new SaxonApiException(e);
        }
    }

    private void transform(Source clMl, Destination destination) throws SaxonApiException {
        XsltTransformer transform = executable.load();
        transform.setSource(clMl);
        transform.setDestination(destination);
        transform.transform();
    }

    public XdmNode transform(InputStream clMl) throws SaxonApiException {
        Source source = new StreamSource(clMl);
        XdmDestination destination = new XdmDestination();
        transform(source, destination);
        return destination.getXdmNode();
    }

    public XdmNode transform(String clMl) throws SaxonApiException {
        ByteArrayInputStream stream = new ByteArrayInputStream(clMl.getBytes());
        return transform(stream);
    }

    static final Properties Properties = new Properties();
    static {
        Properties.setProperty(Serializer.Property.INDENT.toString(), "yes");
    }

    public static String serialize(XdmNode akn) throws SaxonApiException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Serializer serializer = akn.getProcessor().newSerializer(output);
        serializer.setOutputProperties(Properties);
        serializer.serialize(akn.asSource());
        return output.toString(StandardCharsets.UTF_8);
    }

}
