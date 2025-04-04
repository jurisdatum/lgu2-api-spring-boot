package uk.gov.legislation.endpoints;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import uk.gov.legislation.data.marklogic.legislation.Legislation;

import java.util.Optional;

public class CustomHeaders {

    static final String TYPE_HEADER = "X-Document-Type";
    static final String YEAR_HEADER = "X-Document-Year";
    static final String NUMBER_HEADER = "X-Document-Number";
    static final String VERSION_HEADER = "X-Document-Version";

    public static HttpHeaders makeHeaders(Optional<Legislation.Redirect> redirect) {
        if (redirect.isEmpty())
            return null;
        return makeHeaders(redirect.get());
    }

    public static HttpHeaders makeHeaders(Legislation.Redirect redirect) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(TYPE_HEADER, redirect.type());
        headers.set(YEAR_HEADER, redirect.year());
        headers.set(NUMBER_HEADER, Integer.toString(redirect.number()));
        headers.set(VERSION_HEADER, redirect.version().orElse("current"));
        return headers;
    }

    public static <T> ResponseEntity<T> ok(T body, Optional<Legislation. Redirect> redirect) {
        HttpHeaders headers = CustomHeaders.makeHeaders(redirect);
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private CustomHeaders() { }

}
