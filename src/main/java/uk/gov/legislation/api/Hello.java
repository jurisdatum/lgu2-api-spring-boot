package uk.gov.legislation.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class Hello {

    @GetMapping(value = "/hello", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello(@RequestParam(value = "name", defaultValue = "world") String name) {
        return String.format("Hello %s!\n", name);
    }

}
