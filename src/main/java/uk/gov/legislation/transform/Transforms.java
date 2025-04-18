package uk.gov.legislation.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.Document;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.api.responses.Fragment;
import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.converters.DocumentMetadataConverter;
import uk.gov.legislation.converters.FragmentMetadataConverter;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class Transforms {

    private final Clml2Akn clml2akn;

    private final Akn2Html akn2html;

    private final Simplify simplifier;

    private final Clml2Docx clml2docx;

    public Transforms(Clml2Akn clml2akn, Akn2Html akn2html, Simplify simplify, Clml2Docx clml2docx) {
        this.clml2akn = clml2akn;
        this.akn2html = akn2html;
        this.simplifier = simplify;
        this.clml2docx = clml2docx;
    }

    public String clml2akn(String clml) throws SaxonApiException {
        // maybe it's more effecient to transform directly to a Serializer
        XdmNode aknNode = clml2akn.transform(clml);
        return Clml2Akn.serialize(aknNode);
    }

    public String clml2html(String clml, boolean standalone) throws SaxonApiException {
        XdmNode akn = clml2akn.transform(clml);
        return akn2html.transform(akn, standalone);
    }

    public Document clml2document(String clml) throws SaxonApiException, JsonProcessingException {
        XdmNode doc = Helper.parse(clml);
        XdmNode akn = clml2akn.transform(doc);
        String html = akn2html.transform(akn, false);
        Metadata simple = simplifier.extractDocumentMetadata(doc);
        DocumentMetadata converted = DocumentMetadataConverter.convert(simple);
        return new Document(converted, html);
    }

    public Fragment clml2fragment(String clml) throws SaxonApiException, JsonProcessingException {
        XdmNode doc = Helper.parse(clml);
        XdmNode akn = clml2akn.transform(doc);
        String html = akn2html.transform(akn, false);
        Metadata simple = simplifier.extractFragmentMetadata(doc);
        FragmentMetadata converted = FragmentMetadataConverter.convert(simple);
        return new Fragment(converted, html);
    }

    public byte[] clml2docx(String clml) throws IOException, SaxonApiException {
        ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
        return clml2docx.transform(input);
    }

}
