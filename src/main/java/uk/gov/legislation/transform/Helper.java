package uk.gov.legislation.transform;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.serialize.Emitter;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

public class Helper {

    public static final Processor processor = new Processor(false);

    public static class SerializerFactory extends net.sf.saxon.lib.SerializerFactory {

        public SerializerFactory(Configuration config) {
            super(config);
        }

        @Override
        protected Emitter newXMLEmitter(Properties properties) {
            return new XMLEmitter();
        }

    }

    public static class XMLEmitter extends net.sf.saxon.serialize.XMLEmitter {

        @Override
        protected void writeAttributeIndentString() throws IOException {
            this.writer.writeCodePoint(32);
        }

    }

    /**
     * The purpose of this is to make generate-id() stable, for testing. Without it generate-id()
     * produces different values depending on the order the source document is processed.
     */
    public static class DocumentNumberAllocator extends net.sf.saxon.tree.util.DocumentNumberAllocator {

        @Override
        public synchronized long allocateDocumentNumber() {
            return 1;
        }

    }

    static {
        Configuration configuration = processor.getUnderlyingConfiguration();
        SerializerFactory serializerFactory = new SerializerFactory(configuration);
        configuration.setSerializerFactory(serializerFactory);
        DocumentNumberAllocator numberAllocator = new DocumentNumberAllocator();
        configuration.setDocumentNumberAllocator(numberAllocator);
    }

    public static XdmNode parse(String xml) throws SaxonApiException {
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        Source source = new StreamSource(stream);
        DocumentBuilder builder = processor.newDocumentBuilder();
        return builder.build(source);
    }

}
