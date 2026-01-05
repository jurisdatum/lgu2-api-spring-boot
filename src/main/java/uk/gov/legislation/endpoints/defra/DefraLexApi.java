package uk.gov.legislation.endpoints.defra;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.legislation.api.parameters.*;
import uk.gov.legislation.data.virtuoso.defra.Response;

import java.util.concurrent.CompletionStage;

@Tag(name = "Linked Data")
@RequestMapping("/defra")
public interface DefraLexApi {

    @GetMapping(path ="/items", produces = "application/json")
    CompletionStage<Response> x(
        @RequestParam(required = false) Boolean inForce,
        @RequestParam(required = false) Boolean isCommencementOrder,
        @RequestParam(required = false) Boolean isRevocationOrder,
        @RequestParam(required = false) @Type String type,
        @RequestParam(required = false) @Year Integer year,
        @RequestParam(required = false) @DefraChapter String chapter,
        @RequestParam(required = false) @DefraExtent String extent,
        @RequestParam(required = false) @DefraSource String source,
        @RequestParam(required = false) @DefraRegulator String regulator,
        @RequestParam(required = false) @DefraSubject String subject,
        @RequestParam(required = false) @DefraReviewYear Integer review,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize);
}
