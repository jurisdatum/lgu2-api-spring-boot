package uk.gov.legislation.data.marklogic;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.stereotype.Component;
import uk.gov.legislation.exceptions.MarkLogicRequestException;

@Component
public class MarkLogic {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUri;
    private final String authHeader;

    /** Using constructor-based injection for better testability and immutability. */
    public MarkLogic(MarkLogicConfig config) {
        this.baseUri = String.format("http://%s:%d/queries/", config.host(), config.port());
        this.authHeader = createAuthHeader(config.username(), config.password());
    }

    private String createAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials =
                Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }

    /**
     * Executes a GET request to the specified MarkLogic endpoint with query parameters.
     *
     * @param endpoint the MarkLogic endpoint path
     * @param query the query parameters to append to the request URI
     * @return the response body as a String
     * @throws MarkLogicRequestException if the response status code indicates an error
     */
    public String get(String endpoint, String query) throws IOException, InterruptedException {
        URI uri = URI.create(baseUri + endpoint + query);
        HttpRequest request =
                HttpRequest.newBuilder().uri(uri).header("Authorization", authHeader).build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new MarkLogicRequestException(
                    "Error response from MarkLogic: " + response.body());
        }
        return response.body();
    }

    /**
     * Internal helper intended for data-layer callers that need to stream the raw response body.
     * External callers should prefer higher-level APIs in {@code
     * uk.gov.legislation.data.marklogic.legislation}.
     *
     * @param endpoint endpoint path relative to {@code /queries/}
     * @param query query string to append (must include leading {@code ?})
     * @return {@link PushbackInputStream} wrapping the HTTP response body; caller must close it
     * @throws MarkLogicRequestException when MarkLogic responds with a 4xx/5xx status
     */
    public PushbackInputStream getStream(String endpoint, String query)
            throws IOException, InterruptedException {
        URI uri = URI.create(baseUri + endpoint + query);
        HttpRequest request =
                HttpRequest.newBuilder().uri(uri).header("Authorization", authHeader).build();
        HttpResponse<InputStream> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        InputStream bodyStream = response.body();
        if (response.statusCode() >= 400) {
            try (bodyStream) { // closes the stream even though we’re about to throw
                String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
                throw new MarkLogicRequestException("Error response from MarkLogic: " + body);
            }
        }
        return new PushbackInputStream(bodyStream, 1024);
    }

    /* status checks for health endpoint with shorter timeouts */

    private final HttpClient statusClient =
            HttpClient.newBuilder().connectTimeout(Duration.ofMillis(300)).build();

    public int getStatus(String endpoint, String query) throws IOException, InterruptedException {
        URI uri = URI.create(baseUri + endpoint + query);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Authorization", authHeader)
                        .timeout(Duration.ofMillis(800))
                        .build();
        HttpResponse<Void> response =
                statusClient.send(request, HttpResponse.BodyHandlers.discarding());
        return response.statusCode();
    }
}
