package uk.gov.legislation.endpoints.contact;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Internal", description = "Endpoints requiring API key authentication")
@RequestMapping(path = "/contact")
@SecurityRequirement(name = "apiKey")
public class ContactController {

    @PostMapping(value = "/tso", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendEmailToTso(@Valid @RequestBody ContactRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

}
