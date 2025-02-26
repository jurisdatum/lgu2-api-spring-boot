package uk.gov.legislation.data.virtuoso;

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

    public String query(String query, String format, boolean isGetRequest) throws IOException, InterruptedException {
        return executeQuery(query, format, isGetRequest);
    }

    public JsonResults query(String query) throws IOException, InterruptedException {
        String response = executeQuery(query, "json", false);
        return JsonResults.parse(response);
    }

    private String executeQuery(String query, String format, boolean isGetRequest) throws IOException, InterruptedException {
        String acceptHeader = switch (format.toLowerCase()) {
            case "xml" -> "application/sparql-results+xml";
            case "json" -> "application/sparql-results+json";
            case "csv" -> "text/csv";
            case "txt", "text" -> "text/plain";
            default -> "application/sparql-results+json";
        };

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .header("Accept", acceptHeader);
        if (isGetRequest) {
            URI getUri = URI.create(VIRTUSO_URL + "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
            requestBuilder.uri(getUri).GET();
        } else {
            String body = "query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            requestBuilder.uri(VIRTUSO_URL)
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
}
