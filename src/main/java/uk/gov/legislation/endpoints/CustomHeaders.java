package uk.gov.legislation.endpoints;

import org.springframework.http.HttpHeaders;
import uk.gov.legislation.data.marklogic.legislation.Legislation;

public class CustomHeaders {

    static final String TYPE_HEADER = "X-Document-Type";
    static final String YEAR_HEADER = "X-Document-Year";
    static final String NUMBER_HEADER = "X-Document-Number";
    static final String VERSION_HEADER = "X-Document-Version";

    public static HttpHeaders make(String language, Legislation.Redirect redirect) {
        HttpHeaders headers = new HttpHeaders();
        if (language != null) {
            headers.set(HttpHeaders.CONTENT_LANGUAGE, language);
        }
        if (redirect != null) {
            headers.set(TYPE_HEADER, redirect.type());
            headers.set(YEAR_HEADER, redirect.year());
            headers.set(NUMBER_HEADER, Long.toString(redirect.number()));
            headers.set(VERSION_HEADER, redirect.version().orElse("current"));
        }
        return headers;
    }

    private CustomHeaders() { }

}
