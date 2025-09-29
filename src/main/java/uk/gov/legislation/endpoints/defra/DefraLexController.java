package uk.gov.legislation.endpoints.defra;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.defra.DefraLex;
import uk.gov.legislation.data.virtuoso.defra.Parameters;
import uk.gov.legislation.data.virtuoso.defra.Response;

import java.util.concurrent.CompletionStage;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(path = "/defra", produces = "application/json")
public class DefraLexController {

    private final DefraLex query;

    public DefraLexController(DefraLex query) {
        this.query = query;
    }

    @GetMapping("/items")
    public CompletionStage<Response> x(
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
            @RequestParam(defaultValue = "20") int pageSize) {
        validateType(type);
        Parameters params = Parameters.builder()
            .inForce(inForce)
            .isCommencementOrder(isCommencementOrder)
            .isRevocationOrder(isRevocationOrder)
            .type(type)
            .year(year)
            .chapter(chapter)
            .extent(extent)
            .source(source)
            .regulator(regulator)
            .subject(subject)
            .review(review)
            .page(page)
            .pageSize(pageSize)
            .build();
        return query.fetch(params);
    }

}
