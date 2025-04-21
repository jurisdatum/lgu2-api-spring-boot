package uk.gov.legislation.endpoints.documents;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.data.marklogic.search.LastUpdated;
import uk.gov.legislation.data2.SearchService;

import java.time.ZonedDateTime;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class DocumentsController implements DocumentsApi {

    private final SearchService search;

    private final ContentNegotiationManager negotiation;

    public DocumentsController(SearchService search, ContentNegotiationManager negotiation) {
        this.search = search;
        this.negotiation = negotiation;
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocs(String type, int page) throws Exception {
        validateType(type);
        PageOfDocuments docs = search.byType(type, page);
        return ResponseEntity.ok(docs);
    }

    @Override
    public ResponseEntity<String> getFeed(String type, int page) throws Exception {
        validateType(type);
        String atom = search.byTypeAtom(type, page);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocsByTypeAndYear(String type, int year, int page) throws Exception {
        validateType(type);
        PageOfDocuments docs = search.byTypeAndYear(type, year, page);
        return ResponseEntity.ok(docs);
    }

    @Override
    public ResponseEntity<String> getFeedByTypeAndYear(String type, int year, int page) throws Exception {
        validateType(type);
        String atom = search.byTypeAndYearAtom(type, year, page);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<Object> getNew(NativeWebRequest request, String region, int page) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        if (media.equals(MediaType.APPLICATION_ATOM_XML)) {
            String atom = search.getNewAtom(region, page);
            ZonedDateTime updated = LastUpdated.get(atom);
            return ResponseEntity.ok()
                .lastModified(updated)
                .body(atom);
        }
        PageOfDocuments json = search.getNew(region, page);
        return ResponseEntity.ok()
            .lastModified(json.meta.updated)
            .body(json);
    }

}
