package uk.gov.legislation.data.virtuoso;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Repository
public class Virtuoso {

    private final String endpoint;

    public Virtuoso(Environment env) {
        String host = env.getProperty("VIRTUOSO_HOST");
        String port = env.getProperty("VIRTUOSO_PORT");

        if (host == null || port == null) {
            throw new IllegalArgumentException("VIRTUOSO_HOST or VIRTUOSO_PORT cannot be null");
        }
        this.endpoint = "http://" + host + ":" + port + "/sparql";
    }

    public static final Set<String> Formats = Set.of(
        "application/rdf+xml",
        "application/rdf+json",
        "application/ld+json",
        "application/sparql-results+json",
        "application/sparql-results+xml",
        "text/csv",
        "text/plain",
        "text/turtle"
    );

    public String query(String query, String format) throws IOException, InterruptedException {
        if (!Formats.contains(format))
            throw new IllegalArgumentException(format);
        URI uri = URI.create(endpoint + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .header("Accept", format)
            .build();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return response.body();
    }

    public JsonResults query(String query) throws IOException, InterruptedException {
        String json = query(query, "application/sparql-results+json");
        return JsonResults.parse(json);
    }

}
