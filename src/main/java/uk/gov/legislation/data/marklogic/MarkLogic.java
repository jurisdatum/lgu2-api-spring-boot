package uk.gov.legislation.data.marklogic;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class MarkLogic {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        String host = env.getProperty("MARKLOGIC_HOST");
        String port = env.getProperty("MARKLOGIC_PORT");
        String username = env.getProperty("MARKLOGIC_USERNAME");
        String password = env.getProperty("MARKLOGIC_PASSWORD");
        Base = "http://" + host + ":" + port + "/";
        Auth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    private String Base;

    private String Auth;

    String get(String endpoint, String query) throws IOException, InterruptedException {
        URI uri = URI.create(Base + endpoint + query);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", Auth).build();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        if (response.statusCode() >= 400)
            throw new RuntimeException(response.body());
        return response.body();
    }

}
