package uk.gov.legislation.data.marklogic.impacts;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.marklogic.Error;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import uk.gov.legislation.transform.simple.SimpleXmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Optional;

@Repository
public class Impacts {

    private static final String ENDPOINT = "impacts.xq";

    private final MarkLogic db;

    public Impacts(MarkLogic db) {
        this.db = db;
    }

    public Optional<String> getXml(int year, int number) throws IOException, InterruptedException {
        String query = "?impacttype=ukia&impactyear=%d&impactnumber=%d".formatted(year, number);
        String xml = db.get(ENDPOINT, query);
        Error.RootClassification classification = Error.classifyRoot(xml);
        if (classification == Error.RootClassification.MALFORMED)
            throw new MarkLogicRequestException("MarkLogic response is not well-formed XML");
        return classification == Error.RootClassification.OTHER ? Optional.of(xml) : Optional.empty();
    }

    public Optional<InputStream> getStream(int year, int number) throws IOException, InterruptedException {
        String query = "?impacttype=ukia&impactyear=%d&impactnumber=%d".formatted(year, number);
        PushbackInputStream stream = db.getStream(ENDPOINT, query);
        try {
            Error.RootClassification classification = Error.classifyRoot(stream);
            // classifyRoot on a PushbackInputStream never returns MALFORMED (see RootClassification),
            // so != OTHER is equivalent to == ERROR here.
            if (classification != Error.RootClassification.OTHER) {
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
