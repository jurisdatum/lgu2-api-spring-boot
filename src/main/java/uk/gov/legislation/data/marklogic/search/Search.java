package uk.gov.legislation.data.marklogic.search;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.MarkLogic;

import java.io.IOException;

@Service
public class Search {

    private static final String ENDPOINT = "search.xq";

    private final MarkLogic db;

    public Search(MarkLogic db) {
        this.db = db;
    }

    public String getAtom(Parameters params) throws IOException, InterruptedException {
        String query = params.toQuery();
        return db.get(ENDPOINT, query);
    }

    public SearchResults get(Parameters params) throws IOException, InterruptedException {
        String atom = getAtom(params);
        return SearchResults.parse(atom);
    }

}
