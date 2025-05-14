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
import java.util.SortedMap;
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

        CompletableFuture<List<TypeFacets.TypeCount>> typeCounts =
            TypeFacets.fetchTypeCounts(this, baseWhere);

        CompletableFuture<SortedMap<Integer, Integer>> yearCounts =
            YearFacets.fetchYearCounts(this, baseWhere);

        CompletableFuture<List<ChapterFacets.ChapterCount>> chapterCounts =
            ChapterFacets.fetchChapterCounts(this, baseWhere);

        CompletableFuture<Void> allDone =
            CompletableFuture.allOf(results, typeCounts, yearCounts);

        Function<Void, Response> assembleResponse = ignored -> {
            Response response = new Response();
            response.counts.byType = typeCounts.join();
            response.counts.byYear = yearCounts.join();
            response.counts.byChapter = chapterCounts.join();
            response.results = results.join();
            return response;
        };

        return allDone.thenApply(assembleResponse);

    }

}
