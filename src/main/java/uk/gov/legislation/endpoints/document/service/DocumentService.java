package uk.gov.legislation.endpoints.document.service;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.endpoints.CustomHeaders;
import uk.gov.legislation.endpoints.document.MetaData;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.ClMl2Akn;

import java.util.Optional;
import java.util.function.Function;

@Service
public class DocumentService {

    private final Legislation legislationService;
    private final ClMl2Akn clMlToAknTransformer;
    private final Akn2Html aknToHtmlTransformer;

    public DocumentService(Legislation legislationService, ClMl2Akn clMlToAknTransformer, Akn2Html aknToHtmlTransformer) {
        this.legislationService = legislationService;
        this.clMlToAknTransformer = clMlToAknTransformer;
        this.aknToHtmlTransformer = aknToHtmlTransformer;
    }

    public String transformToAkn(String clMlContent) throws SaxonApiException {
        XdmNode aknNode = clMlToAknTransformer.transform(clMlContent);
        return ClMl2Akn.serialize(aknNode);
    }

    public String transformToHtml(String clMlContent) throws SaxonApiException {
        XdmNode aknNode = clMlToAknTransformer.transform(clMlContent);
        return aknToHtmlTransformer.transform(aknNode, true);
    }

    public DocumentApi.Response transformToJsonResponse(String clMlContent) throws SaxonApiException {
        XdmNode aknNode = clMlToAknTransformer.transform(clMlContent);
        String htmlContent = aknToHtmlTransformer.transform(aknNode, false);
        MetaData metadata = AkN.Meta.extract(aknNode);
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
        T body = transformationFunction.apply(leg.clMl());
        HttpHeaders headers = leg.redirect().map(CustomHeaders::makeHeaders).orElse(null);
        return ResponseEntity.ok().headers(headers).body(body);
    }

}
