package uk.gov.legislation.endpoints.defra;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.data.virtuoso.defra.Response;

import java.util.concurrent.CompletionStage;

@Tag(name = "Linked Data")
@RequestMapping("/defra")
public interface DefraLexApi {

    @GetMapping(path ="/items", produces = "application/json")
     CompletionStage <Response> x(
        @RequestParam(required = false) Boolean inForce,
        @RequestParam(required = false) Boolean isCommencementOrder,
        @RequestParam(required = false) Boolean isRevocationOrder,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) String chapter,
        @RequestParam(required = false) String extent,
        @RequestParam(required = false) String source,
        @RequestParam(required = false) String regulator,
        @RequestParam(required = false) String subject,
        @RequestParam(required = false) Integer review,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize);
}
