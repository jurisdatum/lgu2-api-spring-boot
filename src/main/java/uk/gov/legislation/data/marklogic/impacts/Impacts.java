package uk.gov.legislation.data.marklogic.impacts;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.transform.simple.SimpleXmlMapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Repository
public class Impacts {

    private static final String ENDPOINT = "impacts.xq";

    private final MarkLogic db;

    public Impacts(MarkLogic db) {
        this.db = db;
    }

    private static final XMLInputFactory factory = XMLInputFactory.newFactory();

    boolean isError(String xml) throws IOException {
        try (ByteArrayInputStream sample = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            XMLStreamReader reader = factory.createXMLStreamReader(sample);
            try {
                return reader.nextTag() == XMLStreamConstants.START_ELEMENT && "error".equals(reader.getLocalName());
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            return false;
        }
    }

    public Optional<String> getXml(int year, int number) throws IOException, InterruptedException {
        String query = "?impacttype=ukia&impactyear=%d&impactnumber=%d".formatted(year, number);
        String xml = db.get(ENDPOINT, query);
        return isError(xml) ? Optional.empty() : Optional.of(xml);
    }

    public Optional<InputStream> getStream(int year, int number) throws IOException, InterruptedException {
        String query = "?impacttype=ukia&impactyear=%d&impactnumber=%d".formatted(year, number);
        PushbackInputStream stream = db.getStream(ENDPOINT, query);
        try {
            Optional<Error> maybeError = Error.parse(stream);
            if (maybeError.isPresent()) {
                stream.close();
                return Optional.empty();
            }
            return Optional.of(stream);
        } catch (IOException | RuntimeException e) {
            try {
                stream.close();
            } catch (IOException closeException) {
                e.addSuppressed(closeException);
            }
            throw e;
        }
    }

    public Optional<ImpactAssessment> get(int year, int number) throws IOException, InterruptedException {
        Optional<String> xml = getXml(year, number);
        if (xml.isEmpty())
            return Optional.empty();
        return Optional.of(SimpleXmlMapper.INSTANCE.readValue(xml.get(), ImpactAssessment.class));
    }

}
