package uk.gov.legislation.endpoints.fragment.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;

import java.util.Optional;
import java.util.function.Function;

@Service
public class FragmentService {

    private final Legislation db;

    public FragmentService(Legislation db) {
        this.db = db;
    }

    /* helper */

    public  <T> ResponseEntity <T> fetchAndTransform(String type, String year, int number, String section, Optional<String> version, Function<String, T> transform) {
        Legislation.Response leg = db.getDocumentSection(type, year, number, section, version);
        T body = transform.apply(leg.clMl());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
