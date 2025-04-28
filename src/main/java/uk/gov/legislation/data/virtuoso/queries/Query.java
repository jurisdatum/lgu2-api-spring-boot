package uk.gov.legislation.data.virtuoso.queries;

class Query {

    static String makeSingleConstructQuery(String uri) {
        return "CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o . }".formatted(uri, uri);
    }

}
