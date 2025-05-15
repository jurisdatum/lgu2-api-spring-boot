package uk.gov.legislation.endpoints.ld.monarch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.legislation.data.virtuoso.Virtuoso;
import uk.gov.legislation.endpoints.ld.monarch.components.GetData;
import uk.gov.legislation.endpoints.ld.monarch.components.GetMappingData;
import uk.gov.legislation.endpoints.ld.monarch.api.MonarchApi;

@RestController
public class MonarchController implements MonarchApi {

    private final ContentNegotiationManager negotiationManager;
    private final GetData getData;
    private final GetMappingData getMappingData;

    public MonarchController(
        ContentNegotiationManager negotiationManager,
        GetData getData,
        GetMappingData getMappingData
    ) {
        this.negotiationManager = negotiationManager;
        this.getData = getData;
        this.getMappingData = getMappingData;
    }

    @Override
    public ResponseEntity<?> getMonarchInfo(NativeWebRequest request, String monarch) throws Exception {
        MediaType mediaType = negotiationManager.resolveMediaTypes(request).getFirst();

        if (Virtuoso.Formats.contains(mediaType.toString())) {
            String result = getData.apply(monarch, mediaType.toString());
            return ResponseEntity.ok().contentType(mediaType).body(result);
        }

        return getMappingData.apply(monarch)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}











