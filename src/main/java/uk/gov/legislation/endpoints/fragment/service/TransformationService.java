package uk.gov.legislation.endpoints.fragment.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.converters.FragmentMetadataConverter;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Helper;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

@Service
public class TransformationService {

    private final Clml2Akn clml2akn;
    private final Akn2Html akn2html;
    private final Simplify simplifier;

    public TransformationService(Clml2Akn clml2akn, Akn2Html akn2html, Simplify simplifier) {
        this.clml2akn = clml2akn;
        this.akn2html = akn2html;
        this.simplifier = simplifier;
    }

    private final Logger logger = LoggerFactory.getLogger(TransformationService.class);

    // FixMe method in DocumentService throws SaxonApiException
    public String transformToAkn(String clml) {
        try {
            return Clml2Akn.serialize(clml2akn.transform(clml));
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming CLML to AKN format", e);
        }
    }

    // FixMe signature should match DocumentService
    public String transformToHtml(String clml, boolean standalone) {
        try {
            XdmNode akn = clml2akn.transform(clml);
            return akn2html.transform(akn, standalone);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error transforming AKN to HTML format", e);
        }
    }

    public Fragment transformToJsonResponse(String clml) {
        try {
            long start = System.currentTimeMillis();
            XdmNode clmlDoc = Helper.parse(clml);
            XdmNode akn = clml2akn.transform(clmlDoc);
            String html = akn2html.transform(akn, false);
            Metadata meta = simplifier.extractFragmentMetadata(clmlDoc);
            FragmentMetadata convertedMetadata = FragmentMetadataConverter.convert(meta);
            long end = System.currentTimeMillis();
            logger.debug("It took {} miliseconds to convert CLML to JSON", end - start);
            return new Fragment(convertedMetadata, html);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error creating JSON response from CLML", e);
        } catch (JsonProcessingException e) {
            throw new TransformationException("Simplification to JSON format failed", e);
        }
    }

}
