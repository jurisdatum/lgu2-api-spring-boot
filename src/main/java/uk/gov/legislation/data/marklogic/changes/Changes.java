package uk.gov.legislation.data.marklogic.changes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import uk.gov.legislation.data.marklogic.MarkLogic;

import java.io.IOException;

@Repository
public class Changes {

    private static final String ENDPOINT = "changes.xq";

    private final MarkLogic db;

    public Changes(MarkLogic db) {
        this.db = db;
    }

    private final Logger logger = LoggerFactory.getLogger(Changes.class);

    public String fetch(Parameters params) throws IOException, InterruptedException {
        String query = params.toQuery();
        logger.debug("fetching changes with query {}", query);
        long start = System.currentTimeMillis();
        String atom = db.get(ENDPOINT, query);
        long end = System.currentTimeMillis();
        logger.debug("it took {} milliseconds to fetch changes", end - start);
        return atom;
    }

}
