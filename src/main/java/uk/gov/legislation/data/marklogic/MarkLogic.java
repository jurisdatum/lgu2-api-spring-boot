package uk.gov.legislation.data.marklogic;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class MarkLogic {

    private final HttpClient httpClient;
    private final String baseUri;
    private final String authHeader;

    /**
     * Using constructor-based injection for better testability and immutability.
     */
    public MarkLogic(Environment env) {
        this.httpClient = HttpClient.newHttpClient();

        String host = env.getProperty("MARKLOGIC_HOST");
        String port = env.getProperty("MARKLOGIC_PORT");
        String username = env.getProperty("MARKLOGIC_USERNAME");
        String password = env.getProperty("MARKLOGIC_PASSWORD");

        this.baseUri = String.format("http://%s:%s/", host, port);
        this.authHeader = createAuthHeader(username, password);
    }

    private String createAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    /**
     * Executes a GET request to the specified MarkLogic endpoint with query parameters.
     *
     * @param endpoint the MarkLogic endpoint path
     * @param query    the query parameters to append to the request URI
     * @return the response body as a String
     * @throws MarkLogicRequestException if the response status code indicates an error
     */
    public String get(String endpoint, String query) throws IOException, InterruptedException {
        URI uri = URI.create(baseUri + endpoint + query);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", authHeader)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new MarkLogicRequestException("Error response from MarkLogic: " + response.body());
        }
        return response.body();
    }
}
