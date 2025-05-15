package uk.gov.legislation.endpoints.ld.monarch.components;

import org.springframework.stereotype.Component;
import uk.gov.legislation.data.virtuoso.Virtuoso;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

public interface GetData {

    String apply(String monarchName, String format) throws Exception;

    @Component
    class Default implements GetData {
        private final Virtuoso virtuoso;

        public Default(Virtuoso virtuoso) {
            this.virtuoso = virtuoso;
        }

        @Override
        public String apply(String monarch, String format) throws Exception {
            String uri = String.format("http://www.legislation.gov.uk/id/monarch/%s", monarch);
            String query = makeSingleConstructQuery(uri);
            return virtuoso.query(query, format);
        }

    }
}


