package uk.gov.legislation.endpoints.ld.interpretation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.InterpretationQuery;

import java.util.Locale;

@RestController
@Tag(name = "Linked Data")
@RequestMapping(
    path = "/ld/interpretation",
    produces = {
        "application/xml",
        "application/json",
        "application/rdf+xml",
        "application/rdf+json",
        "application/ld+json",
        "application/sparql-results+json",
        "application/sparql-results+xml",
        "text/csv",
        "text/plain",
        "text/turtle"
    }
)
public class InterpretationController {

    private final InterpretationQuery query;

    private final ContentNegotiationManager negotiation;

    public InterpretationController(InterpretationQuery query, ContentNegotiationManager negotiation) {
        this.query = query;
        this.negotiation = negotiation;
    }

    @GetMapping("/{type}/{year:\\d{4}}/{number}")
    public ResponseEntity<?> getCalendarYearAndNumber(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable int number,
            @RequestParam(required = false) String version,
            Locale locale) throws Exception {
        return helper(request, type, Integer.toString(year), Integer.toString(number), version, locale);
    }

    @GetMapping("/{type}/{year:\\d{4}}/{date}/{number}")
    public ResponseEntity<?> getYearAndSpecificDate(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable int year,
            @PathVariable String date,
            @PathVariable int number,
            @RequestParam(required = false) String version,
            Locale locale) throws Exception {
        String middle = year + "/" + date;
        return helper(request, type, middle, Integer.toString(number), version, locale);
    }

    @GetMapping("/{type}/{reign}/{session}/{number}")
    public ResponseEntity<?> getRegnalYearAndNumber(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable String reign,
            @PathVariable String session,
            @PathVariable String number,
            @RequestParam(required = false) String version,
            Locale locale) throws Exception {
        String regnal = reign + "/" + session;
        return helper(request, type, regnal, number, version, locale);
    }

    @GetMapping("/{type}/{reign}/{session}/{statute}/{number}")
    public ResponseEntity<?> getRegnalYearWithStatuteNameAndNumber(NativeWebRequest request,
            @PathVariable String type,
            @PathVariable String reign,
            @PathVariable String session,
            @PathVariable String statute,
            @PathVariable String number,
            @RequestParam(required = false) String version,
            Locale locale) throws Exception {
        String middle = reign + "/" + session + "/" + statute;
        return helper(request, type, middle, number, version, locale);
    }

    // this is duplicative of /{type}/{reign}/{session}/{number}, included only for specification
    @GetMapping("/aep/{reign}/{statute}/{number}")
    public ResponseEntity<?> getAEPStatuteNameAndNumber(NativeWebRequest request,
            @PathVariable String reign,
            @PathVariable String statute,
            @PathVariable String number,
            Locale locale) throws Exception {
        String type = "aep";
        String middle = reign + "/" + statute;
        return helper(request, type, middle, number, null, locale);
    }

    // for tempincert URIs with number
    @GetMapping("/aep/{statute}/{number}")
    public ResponseEntity<?> getAEPStatuteNameAndNumber(NativeWebRequest request,
            @PathVariable String statute,
            @PathVariable String number,
            Locale locale) throws Exception {
        String type = "aep";
        String middle = "tempincert/" + statute;
        return helper(request, type, middle, number, null, locale);
    }

    // for tempincert URIs without number
    @GetMapping("/aep/{statute}")
    public ResponseEntity<?> getAEPStatuteNameAlone(NativeWebRequest request,
            @PathVariable String statute,
            Locale locale) throws Exception {
        String type = "aep";
        String middle = "tempincert/" + statute;
        return helper(request, type, middle, null, null, locale);
    }

    @GetMapping("/eut/{treaty}")
    public ResponseEntity<?> getEuropeanUnionTreaty(NativeWebRequest request,
            @PathVariable String treaty,
            @RequestParam(required = false) String version,
            Locale locale) throws Exception {
        return helper(request, "eut", treaty, null, version, locale);
    }

    private ResponseEntity<?> helper(NativeWebRequest request,
            String type,
            String middle,
            String number,
            String version,
            Locale locale) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        boolean welsh = "cy".equals(locale.getLanguage());
        if (Virtuoso.Formats.contains(media.toString())) {
            String data = query.get(type, middle, number, version, welsh, media.toString());
            return ResponseEntity.ok(data);
        }
        // only two possibilities remain: application/json and application/xml
        return query.get(type, middle, number, version, welsh)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
