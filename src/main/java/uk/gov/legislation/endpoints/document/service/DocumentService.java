package uk.gov.legislation.endpoints.document.service;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
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

    public String transformToAkn(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return Clml2Akn.serialize(aknNode);
    }

    public String transformToHtml(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        return aknToHtmlTransformer.transform(aknNode, true);
    }

    public DocumentApi.Response transformToJsonResponse(String clmlContent) throws SaxonApiException {
        XdmNode aknNode = clmlToAknTransformer.transform(clmlContent);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
        Metadata metadata = AkN.Meta.extract(aknNode);
        return new DocumentApi.Response(metadata, htmlContent);
    }

    /* helper */

    public <T> ResponseEntity <T> fetchAndTransform(
            Function <String, T> transformationFunction,
            String type,
            String year,
            int number,
            Optional<String> version) {
        Legislation.Response leg = legislationService.getDocument(type, year, number, version);
        T body = transformationFunction.apply(leg.clml());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
