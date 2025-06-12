package uk.gov.legislation.data.marklogic;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.legislation.exceptions.MarkLogicRequestException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "marklogic")
@Validated
public class MarkLogic {

    @NotBlank private String host;
    @NotBlank private String username;
    @NotBlank private String password;
              private long port;

    private HttpClient httpClient;
    private String baseUri;
    private String authHeader;

    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUri = String.format("http://%s:%s/queries/", host, port);
        this.authHeader = createAuthHeader(username, password);
    }

    private String createAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    // setters — for binding!
    public void setHost(String host) {this.host = host;
    }
    public void setUsername(String username) {this.username = username;
    }
    public void setPassword(String password) {this.password = password;
    }
    public void setPort(long port) {this.port = port;
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
