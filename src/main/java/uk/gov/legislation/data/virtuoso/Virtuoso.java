package uk.gov.legislation.data.virtuoso;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class Virtuoso {

    private static final URI URL = URI.create("https://www.legislation.gov.uk/sparql");
    private static final String USERNAME = System.getenv("VIRTUOSO_USERNAME");
    private static final String PASSWORD = System.getenv("VIRTUOSO_PASSWORD");
    private static final String AUTH = "Basic " + Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());

    static JsonResults query(String query) throws IOException, InterruptedException {
        String body = "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(URL)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("Authorization", AUTH)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "application/sparql-results+json")
            .build();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        return JsonResults.parse(response.body());
    }

}
