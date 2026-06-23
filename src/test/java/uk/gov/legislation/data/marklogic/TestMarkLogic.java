package uk.gov.legislation.data.marklogic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Base MarkLogic stub for tests. Supplies the minimal config the real MarkLogic constructor needs.
 * Subclasses override {@link #get} or {@link #getStream} as needed; both throw by default so tests
 * fail loudly if they hit an unmocked path.
 */
public class TestMarkLogic extends MarkLogic {

    public TestMarkLogic() {
        super(new MarkLogicConfig("localhost", 8080, "user", "password"));
    }

    @Override
    public String get(String endpoint, String query) throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PushbackInputStream getStream(String endpoint, String query)
            throws IOException, InterruptedException {
        throw new UnsupportedOperationException();
    }

    public static PushbackInputStream asStream(String xml) {
        return new PushbackInputStream(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), 1024);
    }
}
