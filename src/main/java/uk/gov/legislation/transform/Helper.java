package uk.gov.legislation.transform;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.serialize.Emitter;

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

    static {
        Configuration configuration = processor.getUnderlyingConfiguration();
        SerializerFactory serializerFactory = new SerializerFactory(configuration);
        configuration.setSerializerFactory(serializerFactory);
    }

}
