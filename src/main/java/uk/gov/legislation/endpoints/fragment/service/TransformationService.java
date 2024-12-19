package uk.gov.legislation.endpoints.fragment.service;


import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.stereotype.Service;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.ClMl2Akn;

@Service
public class TransformationService {

    private final ClMl2Akn clMl2akn;
    private final Akn2Html akn2html;

    public TransformationService(ClMl2Akn clMl2akn, Akn2Html akn2html) {
        this.clMl2akn = clMl2akn;
        this.akn2html = akn2html;
    }

    public String transformToAkn(String clMl) {
        try {
            return ClMl2Akn.serialize(clMl2akn.transform(clMl));
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming CLML to AKN format", e);
        }
    }

    public String transformToHtml(String clMl, boolean includeExtras) {
        try {
            XdmNode akn = clMl2akn.transform(clMl);
            return akn2html.transform(akn, includeExtras);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming AKN to HTML format", e);
        }
    }

    public DocumentApi.Response createJsonResponse(String clMl) {
        try {
            XdmNode akn = clMl2akn.transform(clMl);
            String html = akn2html.transform(akn, false);
            AkN.Meta meta = AkN.Meta.extract(akn);
            return new DocumentApi.Response(meta, html);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error creating JSON response from CLML", e);
        }
    }

}
