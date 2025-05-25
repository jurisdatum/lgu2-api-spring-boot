package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    private final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public DefraLex(Environment env) {
        String host = env.getProperty("VIRTUOSO_HOST");
        String port = env.getProperty("DEFRALEX_PORT");
        this.endpoint = "http://" + host + ":" + port + "/sparql";
    }

    private static final String PREFIXES = """
        PREFIX :     <http://www.legislation.gov.uk/def/legislation/>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    """;

    private static final String BASE_WHERE = """
        ?item  a  :Item ;
               <http://defra-lex.legislation.gov.uk/def/type>  ?type ;
               :year   ?year ;
               :number ?number ;
               :title  ?title .
        ?type  rdfs:label  ?typeLabel .
        %s
    """;

    private static final String RESULTS_QUERY = PREFIXES + """
        SELECT ?item ?year ?number ?title ?typeLabel
        WHERE  {
          { SELECT DISTINCT ?item
            WHERE  { %s }                         # base pattern + filters
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

    static final String FACET_QUERY = PREFIXES + """
        SELECT %s (COUNT(DISTINCT ?item) AS ?cnt)
        WHERE { %s }
        GROUP BY %s
        ORDER BY DESC(?cnt)
    """;

    CompletableFuture<HttpResponse<String>> getSparqlResultsJson(String query) {
        URI uri = URI.create(endpoint + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
            .header("Accept", "application/sparql-results+json").build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    CompletableFuture<ArrayNode> getSparqlBindings(String query) {
        URI uri = URI.create(endpoint + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
            .header("Accept", "application/sparql-results+json").build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(json -> {
                JsonNode tree;
                try {
                    tree = mapper.readTree(json);
                } catch (JsonProcessingException e) {
                    throw new CompletionException(e);
                }
                return (ArrayNode) tree.get("results").get("bindings");
            });
    }

    private CompletableFuture<List<SparqlResults.SimpleItem>> fetchMainResults() {
        String where = BASE_WHERE.formatted("");
        String resultsQuery = RESULTS_QUERY.formatted(where, 10, 0);
        return getSparqlResultsJson(resultsQuery)
            .thenApply(HttpResponse::body)
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

    public CompletionStage<Response> fetchItems() {

        CompletableFuture<List<SparqlResults.SimpleItem>> results = fetchMainResults();

        String baseWhere = "?item a :Item .";

        String status = "http://defra-lex.legislation.gov.uk/def/status";
        CompletableFuture<List<LabeledFacets.Count>> byStatus =
            LabeledFacets.fetch(this, baseWhere, status);

        final String type = "http://defra-lex.legislation.gov.uk/def/type";
        CompletableFuture<List<LabeledFacets.Count>> typeCounts =
            LabeledFacets.fetch(this, baseWhere, type);

        CompletableFuture<List<YearFacets.Count>> yearCounts =
            YearFacets.fetch(this, baseWhere);

        final String chapter = "http://defra-lex.legislation.gov.uk/def/chapter";
        CompletableFuture<List<LabeledFacets.Count>> chapterCounts =
            LabeledFacets.fetch(this, baseWhere, chapter);

        final String extent = "http://defra-lex.legislation.gov.uk/def/extent";
        CompletableFuture<List<LabeledFacets.Count>> byExtent =
            LabeledFacets.fetch(this, baseWhere, extent);

        final String source = "http://defra-lex.legislation.gov.uk/def/sourceOrigin";
        CompletableFuture<List<LabeledFacets.Count>> sourceCounts =
            LabeledFacets.fetch(this, baseWhere, source);

        final String isRegulatedBy = "http://defra-lex.legislation.gov.uk/def/isRegulatedBy";
        CompletableFuture<List<LabeledFacets.Count>> regulatorCounts =
            LabeledFacets.fetch(this, baseWhere, isRegulatedBy, LabeledFacets.PREF_LABEL);

        final String legContents = "http://defra-lex.legislation.gov.uk/def/legislativecontents";
        CompletableFuture<List<LabeledFacets.Count>> bySubject =
            LabeledFacets.fetch(this, baseWhere, legContents);

        final String reviewYear = "http://defra-lex.legislation.gov.uk/def/reviewYear";
        CompletableFuture<List<YearFacets.Count>> byReviewDate =
            YearFacets.fetch(this, baseWhere, reviewYear);

        CompletableFuture<Void> allDone =
            CompletableFuture.allOf(
                results, byStatus, typeCounts, yearCounts, chapterCounts,
                byExtent, sourceCounts, regulatorCounts, bySubject, byReviewDate
            );

        Function<Void, Response> assembleResponse = ignored -> {
            Response response = new Response();
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
