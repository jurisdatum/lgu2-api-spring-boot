package uk.gov.legislation.data.virtuoso;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


@Component
public class Virtuoso {

    private final URI VIRTUOSO_URL;

    public Virtuoso(Environment env) {
        String host = env.getProperty("VIRTUOSO_HOST");
        String port = env.getProperty("VIRTUOSO_PORT");

        if (host == null || port == null) {
            throw new IllegalArgumentException("VIRTUOSO_HOST or VIRTUOSO_PORT cannot be null");
        }
        this.VIRTUOSO_URL = URI.create("http://" + host + ":" + port + "/sparql");
    }

    // Query method called from metadata api
    public JsonResults query(String query) throws IOException, InterruptedException {
        String body = "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(VIRTUOSO_URL)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/sparql-results+json")
                .build();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return JsonResults.parse(response.body());
    }

    // Query method called from sparql api
    public String query(String query, boolean isGetRequest, HttpServletRequest requests) throws IOException, InterruptedException {
        String acceptHeader = determineAcceptHeader(requests);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .header("Accept", acceptHeader);

        if (isGetRequest) {
            URI getUri = URI.create(VIRTUOSO_URL + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
            requestBuilder.uri(getUri).GET();
        } else {
            String body = "query=" + query;
            requestBuilder.uri(URI.create(String.valueOf(VIRTUOSO_URL)))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/x-www-form-urlencoded");
        }

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response;

        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        return response.body();
    }

    private String determineAcceptHeader(HttpServletRequest request) {
        if (request == null) {
            return "application/sparql-results+json";
        }

        String format = request.getHeader("Accept");
        if (format == null || format.equals("*/*")) {
            return "application/sparql-results+json"; // Default format
        }

        return switch (format.toLowerCase()) {
            case "application/json" -> "application/sparql-results+json";
            case "application/rdf+xml" -> "application/rdf+xml";
            case "application/sparql-results+xml", "application/xml" -> "application/sparql-results+xml";
            case "text/csv" -> "text/csv";
            case "text/plain" -> "text/plain";
            case "text/turtle" -> "text/turtle";
            default -> throw new IllegalArgumentException("Invalid Header: "+ format); // Default to JSON if unknown
        };
    }
}