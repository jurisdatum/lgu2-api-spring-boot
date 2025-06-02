package uk.gov.legislation.data.virtuoso.queries;

import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.jsonld.Graph;

import java.io.IOException;
import java.util.Optional;

import static uk.gov.legislation.data.virtuoso.queries.Query.makeSingleConstructQuery;

abstract class SingleParameterQuery<T> {

    private final Virtuoso virtuoso;
    private final Class<T> mapping;

    protected SingleParameterQuery(Virtuoso virtuoso, Class<T> mapping) {
        this.virtuoso = virtuoso;
        this.mapping = mapping;
    }

    public abstract String makeUri(String param);

    public String makeSparqlQuery(String param) {
        String uri = makeUri(param);
        return makeSingleConstructQuery(uri);
    }

    public String get(String param, String format) throws IOException, InterruptedException {
        String query = makeSparqlQuery(param);
        return virtuoso.query(query, format);
    }

    public Optional<T> get(String param) throws IOException, InterruptedException {
        String json = get(param, "application/ld+json");
        return Graph.extractFirstObject(json, mapping);
    }

}
