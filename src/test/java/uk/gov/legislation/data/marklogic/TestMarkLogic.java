package uk.gov.legislation.data.marklogic;

import org.springframework.mock.env.MockEnvironment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Base MarkLogic stub for tests. Supplies the minimal property set the real MarkLogic
 * constructor reads. Subclasses override {@link #get} or {@link #getStream} as needed;
 * both throw by default so tests fail loudly if they hit an unmocked path.
 */
public class TestMarkLogic extends MarkLogic {

    public TestMarkLogic() {
        super(new MockEnvironment()
            .withProperty("MARKLOGIC_HOST", "localhost")
            .withProperty("MARKLOGIC_PORT", "8080")
            .withProperty("MARKLOGIC_USERNAME", "user")
            .withProperty("MARKLOGIC_PASSWORD", "password"));
    }

    @Override
    public String get(String endpoint, String query) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PushbackInputStream getStream(String endpoint, String query) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    public static PushbackInputStream asStream(String xml) {
        return new PushbackInputStream(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), 1024);
    }

}
