package uk.gov.legislation.endpoints.search.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "Search")
@Validated
public interface SearchApi {

    @GetMapping(value = "/search")
    ResponseEntity<?> search(
            @RequestParam @NotBlank String title,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader

    ) throws IOException, InterruptedException;
}

