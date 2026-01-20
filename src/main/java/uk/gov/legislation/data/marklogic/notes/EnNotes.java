package uk.gov.legislation.data.marklogic.notes;

import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.marklogic.MarkLogic;
import uk.gov.legislation.transform.simple.SimpleXmlMapper;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.marklogic.impacts.Impacts.isError;

@Repository
public class EnNotes {

    private static final String ENDPOINT = "notes.xq";

    private final MarkLogic db;

    public EnNotes(MarkLogic db) {
        this.db = db;
    }

    public Optional<EN> get(String type, int year, int number)
        throws IOException, InterruptedException {

        Optional <String> xml = getXml(type, year, number);
        if(xml.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(SimpleXmlMapper.INSTANCE.readValue(xml.get(), EN.class));

    }

    public Optional<String> getXml(String type, int year, int number)
        throws IOException, InterruptedException {

        String query =
            "?notes-type=notes&type=%s&year=%d&number=%d"
                .formatted(type, year, number);

        String xml = db.get(ENDPOINT, query);

        return isError(xml) ? Optional.empty() : Optional.of(xml);
    }
}
