package uk.gov.legislation.data.virtuoso;

import jakarta.servlet.http.HttpServletRequest;
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

    public final URI VIRTUSO_URL= URI.create("http://e-leg-poc-lon-nlb-access-553dd9d8d29abe0c.elb.eu-west-2.amazonaws.com:8890/sparql");

    // Query method called from metadata api
    JsonResults query(String query) throws IOException, InterruptedException {
        String body = "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(VIRTUSO_URL)
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
            URI getUri = URI.create(VIRTUSO_URL + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
            requestBuilder.uri(getUri).GET();
        } else {
            String body = "query=" + query;
            requestBuilder.uri(URI.create(String.valueOf(VIRTUSO_URL)))
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
            case "application/xml" -> "application/sparql-results+xml";
            case "text/csv", "text/plain" -> format;
            default -> "application/sparql-results+json";
        };
    }
}