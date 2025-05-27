package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Repository
public class DefraLex {

    private final String endpoint;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public DefraLex(Environment env) {
        String host = env.getProperty("VIRTUOSO_HOST");
        String port = env.getProperty("DEFRALEX_PORT");
        this.endpoint = "http://" + host + ":" + port + "/sparql";
    }

    private static final String RESULTS_QUERY = """
        PREFIX :     <http://www.legislation.gov.uk/def/legislation/>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        SELECT ?item ?year ?number ?title ?typeLabel
        WHERE  {
          { SELECT DISTINCT ?item
            WHERE  {
                %s
                ?item  <http://defra-lex.legislation.gov.uk/def/type>  ?type ;
                    :year   ?year ;
                    :number ?number ;
                    :title  ?title .
                    ?type  rdfs:label  ?typeLabel .            }
            ORDER  BY DESC(?year) ?number
            LIMIT  %d
            OFFSET %d
          }
          # Re‑bind properties for the selected ?item URIs
          ?item  <http://defra-lex.legislation.gov.uk/def/type>  ?type ;
                 :year   ?year ;
                 :number ?number ;
                 :title  ?title .
          ?type  rdfs:label  ?typeLabel .
        }
    """;

    static final String FACET_QUERY = """
        SELECT %s (COUNT(DISTINCT ?item) AS ?cnt)
        WHERE { %s }
        GROUP BY %s
        ORDER BY DESC(?cnt)
    """;

    CompletableFuture<String> getSparqlResultsJson(String query) {
        URI uri = URI.create(endpoint + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
            .header("Accept", "application/sparql-results+json").build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body);
    }

    private CompletableFuture<List<SparqlResults.SimpleItem>> fetchMainResults(String where, int limit, int offset) {
        String query = RESULTS_QUERY.formatted(where, 10, 0);
        return getSparqlResultsJson(query)
            .thenApply(body -> {
                SparqlResults sr;
                try {
                    sr = mapper.readValue(body, SparqlResults.class);
                } catch (JsonProcessingException e) {
                    throw new CompletionException(e);
                }
                return sr.simplified();
            });
    }

    private static class Properties {

        private static final String STATUS = "http://defra-lex.legislation.gov.uk/def/status";

        private static final String TYPE = "http://defra-lex.legislation.gov.uk/def/type";

        private static final String YEAR = "http://www.legislation.gov.uk/def/legislation/year";

        private static final String CHAPTER = "http://defra-lex.legislation.gov.uk/def/chapter";

        private static final String EXTENT = "http://defra-lex.legislation.gov.uk/def/extent";

        private static final String SOURCE = "http://defra-lex.legislation.gov.uk/def/sourceOrigin";

        private static final String IS_REGULATED_BY = "http://defra-lex.legislation.gov.uk/def/isRegulatedBy";

        // "subject"
        private static final String LEG_CONTENTS = "http://defra-lex.legislation.gov.uk/def/legislativecontents";

        private static final String REVIEW_YEAR = "http://defra-lex.legislation.gov.uk/def/reviewYear";

    }

    private static String makeWhereClause(String prop, String uri) {
        return " ?item <" + prop + "> <" + uri +"> .";
    }

    private static String makeWhereClause(String prop, Integer value) {
        String object = "\"" + value + "\"^^<http://www.w3.org/2001/XMLSchema#int>";
        return " ?item <" + prop + "> " + object + " .";
    }

    public CompletionStage<Response> fetch(Parameters params) {

        String where = "?item a <http://www.legislation.gov.uk/def/legislation/Item> .";

        if (params.status != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/status/" + params.status;
            where += makeWhereClause(Properties.STATUS, uri);
        }
        if (params.type != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/type/" + params.type;
            where += makeWhereClause(Properties.TYPE, uri);
        }
        if (params.year != null) {
            where += makeWhereClause(Properties.YEAR, params.year);
        }
        if (params.chapter != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/chapter/" + params.chapter;
            where += makeWhereClause(Properties.CHAPTER, uri);
        }
        if (params.extent != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/extent/" + params.extent;
            where += makeWhereClause(Properties.EXTENT, uri);
        }
        if (params.source != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/origin/" + params.source;
            where += makeWhereClause(Properties.SOURCE, uri);
        }
        if (params.regulator != null) {
            String uri = "http://www.legislation.gov.uk/id/publicbody/" + params.regulator;
            where += makeWhereClause(Properties.IS_REGULATED_BY, uri);
        }
        if (params.subject != null) {
            String uri = "http://defra-lex.legislation.gov.uk/id/activity/" + params.subject;
            where += makeWhereClause(Properties.LEG_CONTENTS, uri);
        }
        if (params.review != null) {
            where += makeWhereClause(Properties.REVIEW_YEAR, params.review);
        }

        // make sure these are set so response contains them
        if (params.pageSize == null)
            params.pageSize = Parameters.DEFAULT_PAGE_SIZE;
        if (params.page == null)
            params.page = Parameters.DEFAULT_PAGE;
        final int offset = (params.page - 1) * params.pageSize;
        var results = fetchMainResults(where, params.pageSize, offset);

        var byStatus = LabeledFacets.fetch(this, where, Properties.STATUS);

        var typeCounts = LabeledFacets.fetch(this, where, Properties.TYPE);

        var yearCounts = YearFacets.fetch(this, where);

        var chapterCounts = LabeledFacets.fetch(this, where, Properties.CHAPTER);

        var byExtent = LabeledFacets.fetch(this, where, Properties.EXTENT);

        var sourceCounts = LabeledFacets.fetch(this, where, Properties.SOURCE);

        var regulatorCounts = LabeledFacets.fetch(this, where, Properties.IS_REGULATED_BY, LabeledFacets.PREF_LABEL);

        var bySubject = LabeledFacets.fetch(this, where, Properties.LEG_CONTENTS);

        var byReviewDate = YearFacets.fetch(this, where, Properties.REVIEW_YEAR);

        var total = Total.get(this, where);

        CompletableFuture<Void> allDone =
            CompletableFuture.allOf(
                results, total, byStatus, typeCounts, yearCounts, chapterCounts,
                byExtent, sourceCounts, regulatorCounts, bySubject, byReviewDate
            );

        Function<Void, Response> assembleResponse = ignored -> {
            Response response = new Response();
            response.query = params;
            response.counts.total = total.join();
            response.counts.byStatus = byStatus.join();
            response.counts.byType = typeCounts.join();
            response.counts.byYear = yearCounts.join();
            response.counts.byChapter = chapterCounts.join();
            response.counts.byExtent = byExtent.join();
            response.counts.bySource = sourceCounts.join();
            response.counts.byRegulator = regulatorCounts.join();
            response.counts.bySubject = bySubject.join();
            response.counts.byReviewDate = byReviewDate.join();
            response.results = results.join();
            return response;
        };

        return allDone.thenApply(assembleResponse);

    }

}
