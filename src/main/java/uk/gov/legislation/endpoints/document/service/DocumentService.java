package uk.gov.legislation.endpoints.document.service;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.Metadata;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;

import java.util.Optional;
import java.util.function.Function;

@Service
public class DocumentService {

    private final Legislation legislationService;
    private final Clml2Akn clmlToAknTransformer;
    private final Akn2Html aknToHtmlTransformer;

    public DocumentService(Legislation legislationService, Clml2Akn clmlToAknTransformer, Akn2Html aknToHtmlTransformer) {
        this.legislationService = legislationService;
        this.clmlToAknTransformer = clmlToAknTransformer;
        this.aknToHtmlTransformer = aknToHtmlTransformer;
    }

    public Optional <String> fetchClmlContent(String type, String year, int number, Optional<String> version) {
        return Optional.ofNullable(legislationService.getDocument(type, year, number, version));
    }

    public String transformToAkn(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return Clml2Akn.serialize(aknNode);
    }

    public String transformToHtml(String clmlContent, boolean isPretty) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return aknToHtmlTransformer.transform(aknNode, isPretty);
    }

    public DocumentApi.Response transformToJsonResponse(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
        Metadata metadata = AkN.Meta.extract(aknNode);
        return new DocumentApi.Response(metadata, htmlContent);
    }

    public <T> ResponseEntity <T> handleTransformation(
            Function <String, T> transformationFunction,
            String type,
            String year,
            int number,
            Optional<String> version,
            String errorMessage) {
        return fetchClmlContent(type, year, number, version)
                .map(transformationFunction)
                .map(result -> ResponseEntity.ok().body(result))
                .orElseThrow(() -> new NoDocumentException(String.format(errorMessage, type, year, number)));
    }
}

