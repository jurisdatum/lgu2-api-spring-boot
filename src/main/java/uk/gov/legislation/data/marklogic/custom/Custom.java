package uk.gov.legislation.data.marklogic.custom;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Repository
public class Custom {

    private final DigestAuthenticator auth;
    private final String url;

    public Custom(Environment env) {
        String host = env.getProperty("MARKLOGIC_HOST");
        String port = "8000";
        String database = "Legislation";
        String username = env.getProperty("MARKLOGIC_USERNAME");
        String password = env.getProperty("MARKLOGIC_PASSWORD");
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
        auth = new DigestAuthenticator(client, username, password);
        url = "http://%s:%s/v1/eval?database=%s".formatted(host, port, database);
    }

    public List<String> query(String xquery) throws IOException, InterruptedException {

        String payload = "xquery=" + URLEncoder.encode(xquery, StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Accept", "multipart/mixed; boundary=BOUNDARY")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build();

        HttpResponse<InputStream> resp =
            auth.send(req, HttpResponse.BodyHandlers.ofInputStream());

        String contentType = resp.headers().firstValue("Content-Type")
            .orElseThrow(() -> new IllegalStateException("No Content-Type"));
        String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);

        try (InputStream body = resp.body()) {
            return MultipartMixedParser.parse(body, boundary).stream()
                .map(part -> part.body)
                .toList();
        }
    }

}
