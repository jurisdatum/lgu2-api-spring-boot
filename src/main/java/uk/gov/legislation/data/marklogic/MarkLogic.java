package uk.gov.legislation.data.marklogic;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class MarkLogic {

    private static final String USERNAME = System.getenv("MARKLOGIC_USERNAME");
    private static final String PASSWORD = System.getenv("MARKLOGIC_PASSWORD");
    private static final String HOST = System.getenv("MARKLOGIC_HOST");
    private static final String PORT = System.getenv("MARKLOGIC_PORT");
    static final String BASE = "http://" + HOST + ":" + PORT + "/";

    private static final String Auth = "Basic " + Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());

    static String get(URI uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", Auth).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 500)
            throw new HttpServerErrorException(HttpStatusCode.valueOf(response.statusCode()));
        if (response.statusCode() >= 400)
            throw new HttpClientErrorException(HttpStatusCode.valueOf(response.statusCode()));
        String xml = response.body();
        return xml;
    }

}
