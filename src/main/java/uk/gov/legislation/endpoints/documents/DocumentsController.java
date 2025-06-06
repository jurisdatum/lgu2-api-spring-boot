package uk.gov.legislation.endpoints.documents;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.legislation.api.responses.PageOfDocuments;
import uk.gov.legislation.converters.DocumentsFeedConverter;
import uk.gov.legislation.data.marklogic.search.LastUpdated;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.data.marklogic.search.Search;
import uk.gov.legislation.data.marklogic.search.SearchResults;

import java.time.ZonedDateTime;

import static uk.gov.legislation.endpoints.ParameterValidator.validateType;

@RestController
public class DocumentsController implements DocumentsApi {

    private final Search search;

    private final ContentNegotiationManager negotiation;

    public DocumentsController(Search search, ContentNegotiationManager negotiation) {
        this.search = search;
        this.negotiation = negotiation;
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocs(String type, int page) throws Exception {
        validateType(type);
        Parameters params = Parameters.builder().type(type).page(page).build();
        SearchResults results = search.get(params);
        PageOfDocuments docs = DocumentsFeedConverter.convert(results);
        return ResponseEntity.ok(docs);
    }

    @Override
    public ResponseEntity<String> getFeed(String type, int page) throws Exception {
        validateType(type);
        Parameters params = Parameters.builder().type(type).page(page).build();
        String atom = search.getAtom(params);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<PageOfDocuments> getDocsByTypeAndYear(String type, int year, int page) throws Exception {
        validateType(type);
        Parameters params = Parameters.builder().type(type).year(year).page(page).build();
        SearchResults results = search.get(params);
        PageOfDocuments docs = DocumentsFeedConverter.convert(results);
        return ResponseEntity.ok(docs);
    }

    @Override
    public ResponseEntity<String> getFeedByTypeAndYear(String type, int year, int page) throws Exception {
        validateType(type);
        Parameters params = Parameters.builder().type(type).year(year).page(page).build();
        String atom = search.getAtom(params);
        return ResponseEntity.ok(atom);
    }

    @Override
    public ResponseEntity<Object> getNew(NativeWebRequest request, String region, int page) throws Exception {
        MediaType media = negotiation.resolveMediaTypes(request).getFirst();
        Parameters params = Parameters.builder()
            .type(region)
            .sort(Parameters.Sort.PUBLISHED)
            .page(page)
            .build();
        if (media.equals(MediaType.APPLICATION_ATOM_XML)) {
            String atom = search.getAtom(params);
            ZonedDateTime updated = LastUpdated.get(atom);
            return ResponseEntity.ok()
                .lastModified(updated)
                .body(atom);
        }
        SearchResults results = search.get(params);
        PageOfDocuments docs = DocumentsFeedConverter.convert(results);
        return ResponseEntity.ok()
            .lastModified(docs.meta.updated)
            .body(docs);
    }

}
