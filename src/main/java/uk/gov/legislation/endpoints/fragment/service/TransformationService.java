package uk.gov.legislation.endpoints.fragment.service;


import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.stereotype.Service;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.AkN;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;

@Service
public class TransformationService {

    private final Clml2Akn clml2akn;
    private final Akn2Html akn2html;

    public TransformationService(Clml2Akn clml2akn, Akn2Html akn2html) {
        this.clml2akn = clml2akn;
        this.akn2html = akn2html;
    }

    public String transformToAkn(String clml) {
        try {
            return Clml2Akn.serialize(clml2akn.transform(clml));
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming CLML to AKN format", e);
        }
    }

    public String transformToHtml(String clml, boolean includeExtras) {
        try {
            XdmNode akn = clml2akn.transform(clml);
            return akn2html.transform(akn, includeExtras);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming AKN to HTML format", e);
        }
    }

    public DocumentApi.Response createJsonResponse(String clml) {
        try {
            XdmNode akn = clml2akn.transform(clml);
            String html = akn2html.transform(akn, false);
            AkN.Meta meta = AkN.Meta.extract(akn);
            return new DocumentApi.Response(meta, html);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error creating JSON response from CLML", e);
        }
    }

}
