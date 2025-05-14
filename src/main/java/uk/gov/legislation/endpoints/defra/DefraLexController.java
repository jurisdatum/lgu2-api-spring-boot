package uk.gov.legislation.endpoints.defra;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.legislation.data.virtuoso.defra.DefraLex;
import uk.gov.legislation.data.virtuoso.defra.Response;

import java.util.concurrent.CompletionStage;

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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
            ) {
        return query.fetchItems();
    }

}
