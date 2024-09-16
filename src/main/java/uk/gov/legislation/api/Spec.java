package uk.gov.legislation.api;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Spec {

    private static final String PATH = "/static/spec.yaml";

    @GetMapping(value = "/spec", produces = { MediaType.TEXT_PLAIN_VALUE, "application/yaml" })
    public byte[] spec() throws Exception {
        return getClass().getResourceAsStream(PATH).readAllBytes();
    }

}
