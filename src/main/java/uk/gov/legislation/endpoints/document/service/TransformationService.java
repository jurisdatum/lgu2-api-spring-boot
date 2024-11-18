package uk.gov.legislation.endpoints.document.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.stereotype.Service;
import uk.gov.legislation.endpoints.document.api.DocumentApi;
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

    public String transformToAkn(String clml) throws SaxonApiException {
        return Clml2Akn.serialize(clml2akn.transform(clml));
    }

    public String transformToHtml(String clml, boolean includeExtras) throws SaxonApiException {
        XdmNode akn = clml2akn.transform(clml);
        return akn2html.transform(akn, includeExtras);
    }

    public DocumentApi.Response createJsonResponse(String clml) throws SaxonApiException {
        XdmNode akn = clml2akn.transform(clml);
        String html = akn2html.transform(akn, false);
        AkN.Meta meta = AkN.Meta.extract(akn);
        return new DocumentApi.Response(meta, html);
    }

    public Object parse(String xml) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(xml, Object.class);
    }
}
