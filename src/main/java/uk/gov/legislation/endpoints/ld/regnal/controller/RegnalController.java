package uk.gov.legislation.endpoints.ld.regnal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.converters.ld.RegnalConverter;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.data.virtuoso.queries.RegnalQuery;
import uk.gov.legislation.endpoints.ld.regnal.api.RegnalApi;

@RestController
public class RegnalController implements RegnalApi {

    private final ContentNegotiationManager negotiation;
    private final RegnalQuery query;

    public RegnalController(ContentNegotiationManager negotiation, RegnalQuery query) {
        this.negotiation = negotiation;
        this.query = query;
    }

    @Override
    public ResponseEntity <?> getRegnalYearInfo(NativeWebRequest request, String reign, Integer regnalYear) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (Virtuoso.Formats.contains(media.toString())) {
            String result = query.fetchRawData(reign,regnalYear, media.toString());
            return ResponseEntity.ok()
                .contentType(media)
                .body(result);
        }
        return query.fetchMappedData(reign, regnalYear)
            .map(RegnalConverter::convert)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
