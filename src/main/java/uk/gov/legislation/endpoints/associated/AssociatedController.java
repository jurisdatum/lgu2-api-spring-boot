package uk.gov.legislation.endpoints.associated;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.api.parameters.Number;
import uk.gov.legislation.api.parameters.Year;
import uk.gov.legislation.api.responses.Associated;
import uk.gov.legislation.converters.ImpactAssessmentConverter;
import uk.gov.legislation.data.marklogic.impacts.Impacts;

import java.io.IOException;

@RestController
public class AssociatedController {

    private final Impacts marklogic;

    public AssociatedController(Impacts marklogic) {
        this.marklogic = marklogic;
    }

    @GetMapping(value = "/associated/ukia/{year}/{number}", produces = "application/xml")
    public String xml(@PathVariable @Year int year, @PathVariable @Number int number) throws IOException, InterruptedException {
        return marklogic.getXml(year, number)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/associated/ukia/{year}/{number}", produces = "application/json")
    public Associated json(@PathVariable @Year int year, @PathVariable @Number int number) throws IOException, InterruptedException {
        return marklogic.get(year, number)
            .map(ImpactAssessmentConverter::convert)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
